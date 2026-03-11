const request = require("../../../utils/request");

const USER_TYPE = {
  STUDENT: 1,
  TEACHER: 2,
};

Page({
  data: {
    userType: null,
    tab: "ongoing",
    activeList: [],
    historyList: [],
    courseIdInput: "",
    loading: false,
  },

  onShow() {
    const userType = wx.getStorageSync("userType");
    this.setData({ userType });
    if (userType === USER_TYPE.STUDENT) {
      this.fetchStudentLists();
    }
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab;
    if (tab && tab !== this.data.tab) {
      this.setData({ tab });
    }
  },

  onCourseIdInput(e) {
    this.setData({ courseIdInput: e.detail.value });
  },

  async fetchStudentLists() {
    if (this.data.loading) {
      return;
    }
    this.setData({ loading: true });
    try {
      const [activeRes, historyRes] = await Promise.all([
        request({ url: "/attendance/myActive", method: "GET" }),
        request({ url: "/attendance/myHistory", method: "GET" }),
      ]);
      this.setData({
        activeList: activeRes.data && activeRes.data.data ? activeRes.data.data : [],
        historyList: historyRes.data && historyRes.data.data ? historyRes.data.data : [],
      });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "加载签到列表失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },

  async onFetchTeacherList() {
    const courseId = this.data.courseIdInput;
    if (!courseId) {
      wx.showToast({ title: "请输入课程ID", icon: "none" });
      return;
    }
    if (this.data.loading) {
      return;
    }
    this.setData({ loading: true });
    try {
      const res = await request({
        url: `/attendance/list/${courseId}`,
        method: "GET",
      });
      const list = res.data && res.data.data ? res.data.data : [];
      const activeList = list.filter((item) => Number(item.status) === 1);
      const historyList = list.filter((item) => Number(item.status) === 0);
      this.setData({ activeList, historyList });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "加载签到列表失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },
});
