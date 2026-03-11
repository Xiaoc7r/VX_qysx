const request = require("../../utils/request");

const USER_TYPE = {
  STUDENT: 1,
  TEACHER: 2,
};

Page({
  data: {
    userType: null,
    history: [],
    stats: null,
    courseIdInput: "",
    teacherStats: null,
    loading: false,
  },

  onShow() {
    const userType = wx.getStorageSync("userType");
    this.setData({ userType });
    if (userType === USER_TYPE.STUDENT) {
      this.fetchStudentData();
    }
  },

  onCourseIdInput(e) {
    this.setData({ courseIdInput: e.detail.value });
  },

  async fetchStudentData() {
    if (this.data.loading) {
      return;
    }
    this.setData({ loading: true });
    try {
      const [historyRes, statsRes] = await Promise.all([
        request({ url: "/attendance/myHistory", method: "GET" }),
        request({ url: "/attendance/student/stats", method: "GET" }),
      ]);
      this.setData({
        history: historyRes.data && historyRes.data.data ? historyRes.data.data : [],
        stats: statsRes.data && statsRes.data.data ? statsRes.data.data : null,
      });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "加载失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },

  async onFetchTeacherStats() {
    const courseId = this.data.courseIdInput;
    if (!courseId) {
      wx.showToast({ title: "请输入课程ID", icon: "none" });
      return;
    }
    try {
      const res = await request({
        url: `/attendance/stats/${courseId}`,
        method: "GET",
      });
      this.setData({
        teacherStats: res.data && res.data.data ? res.data.data : null,
      });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "获取统计失败";
      wx.showToast({ title: message, icon: "none" });
    }
  },
});
