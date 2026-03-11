const request = require("../../utils/request");

const USER_TYPE = {
  STUDENT: 1,
  TEACHER: 2,
};

Page({
  data: {
    userType: null,
    userTypeStudent: USER_TYPE.STUDENT,
    userTypeTeacher: USER_TYPE.TEACHER,
    activeAttendances: [],
    courses: [],
    loading: false,
  },

  onShow() {
    const userId = wx.getStorageSync("userId");
    const userType = wx.getStorageSync("userType");
    this.setData({ userType });
    if (userId && userType) {
      this.fetchCourses(userId, userType);
    }
    this.fetchActiveAttendances();
  },

  async fetchCourses(userId, userType) {
    if (this.data.loading) {
      return;
    }

    this.setData({ loading: true });
    try {
      const res = await request({
        url: `/course/my/${userId}/${userType}`,
        method: "GET",
      });
      const courses = (res.data && res.data.data) || [];
      this.setData({ courses });
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

  async fetchActiveAttendances() {
    try {
      const res = await request({
        url: "/attendance/myActive",
        method: "GET",
      });
      this.setData({
        activeAttendances:
          res.data && res.data.data ? res.data.data : [],
      });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "加载进行中签到失败";
      wx.showToast({ title: message, icon: "none" });
    }
  },
});
