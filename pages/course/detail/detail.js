const request = require("../../../utils/request");

const USER_TYPE = {
  STUDENT: 1,
  TEACHER: 2,
};

Page({
  data: {
    courseId: null,
    userType: null,
    course: null,
    students: [],
    activeAttendance: null,
    history: [],
    loading: false,
  },

  onLoad(options) {
    const courseId = options && (options.courseId || options.id);
    const userType = wx.getStorageSync("userType");
    this.setData({ courseId, userType });
    if (!courseId) {
      wx.showToast({ title: "缺少课程ID", icon: "none" });
      return;
    }
    this.loadCourseDetail();
  },

  async loadCourseDetail() {
    if (this.data.loading) {
      return;
    }

    this.setData({ loading: true });
    try {
      await Promise.all([
        this.fetchCourse(),
        this.fetchHistory(),
        this.fetchRoleData(),
      ]);
    } finally {
      this.setData({ loading: false });
    }
  },

  async fetchCourse() {
    const res = await request({
      url: `/course/${this.data.courseId}`,
      method: "GET",
    });
    this.setData({ course: res.data && res.data.data });
  },

  async fetchHistory() {
    const res = await request({
      url: `/attendance/list/${this.data.courseId}`,
      method: "GET",
    });
    this.setData({ history: res.data && res.data.data ? res.data.data : [] });
  },

  async fetchRoleData() {
    if (this.data.userType === USER_TYPE.TEACHER) {
      const res = await request({
        url: `/course/${this.data.courseId}/students`,
        method: "GET",
      });
      this.setData({
        students: res.data && res.data.data ? res.data.data : [],
      });
      return;
    }

    if (this.data.userType === USER_TYPE.STUDENT) {
      const res = await request({
        url: "/attendance/myActive",
        method: "GET",
      });
      const list = res.data && res.data.data ? res.data.data : [];
      const active = Array.isArray(list)
        ? list.find((item) => `${item.course_id}` === `${this.data.courseId}`)
        : list;
      this.setData({ activeAttendance: active || null });
    }
  },

  async onCreateAttendance() {
    try {
      await request({
        url: "/attendance/create",
        method: "POST",
        data: { courseId: this.data.courseId },
      });
      wx.showToast({ title: "已发起签到", icon: "success" });
      this.fetchHistory();
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "发起签到失败";
      wx.showToast({ title: message, icon: "none" });
    }
  },

  onSignNow() {
    wx.getLocation({
      type: "gcj02",
      success: async (res) => {
        try {
          await request({
            url: "/attendance/submit",
            method: "POST",
            data: {
              courseId: this.data.courseId,
              latitude: res.latitude,
              longitude: res.longitude,
            },
          });
          wx.showToast({ title: "签到成功", icon: "success" });
          this.loadCourseDetail();
        } catch (err) {
          const message =
            err && err.data && err.data.message
              ? err.data.message
              : "签到失败";
          wx.showToast({ title: message, icon: "none" });
        }
      },
      fail: () => {
        wx.showToast({ title: "获取位置失败", icon: "none" });
      },
    });
  },
});
