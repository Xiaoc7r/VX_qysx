const request = require("../../../utils/request");

Page({
  data: {
    tab: "my",
    userId: null,
    userType: null,
    myCourses: [],
    allCourses: [],
    loading: false,
  },

  onShow() {
    const userId = wx.getStorageSync("userId");
    const userType = wx.getStorageSync("userType");
    this.setData({ userId, userType });
    this.fetchMyCourses();
    this.fetchAllCourses();
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab;
    if (tab && tab !== this.data.tab) {
      this.setData({ tab });
    }
  },

  async fetchMyCourses() {
    const { userId, userType } = this.data;
    if (!userId || !userType || this.data.loading) {
      return;
    }
    this.setData({ loading: true });
    try {
      const res = await request({
        url: `/course/my/${userId}/${userType}`,
        method: "GET",
      });
      this.setData({
        myCourses: res.data && res.data.data ? res.data.data : [],
      });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "课程加载失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },

  async fetchAllCourses() {
    if (this.data.loading) {
      return;
    }
    this.setData({ loading: true });
    try {
      const res = await request({
        url: "/course/list",
        method: "GET",
      });
      this.setData({
        allCourses: res.data && res.data.data ? res.data.data : [],
      });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "课程加载失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },
});
