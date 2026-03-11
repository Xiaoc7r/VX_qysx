const request = require("../../../utils/request");

Page({
  data: {
    recordId: null,
    summary: null,
    absentList: [],
    loading: false,
  },

  onLoad(options) {
    const recordId = options && (options.id || options.recordId);
    this.setData({ recordId });
    if (!recordId) {
      wx.showToast({ title: "缺少签到ID", icon: "none" });
      return;
    }
    this.fetchDetail();
  },

  async fetchDetail() {
    if (this.data.loading) {
      return;
    }

    this.setData({ loading: true });
    try {
      const res = await request({
        url: `/attendance/detail/${this.data.recordId}`,
        method: "GET",
      });
      const detail = res.data && res.data.data ? res.data.data : {};
      const absentList =
        detail.absentList ||
        detail.absentStudents ||
        detail.absent_users ||
        [];
      this.setData({
        summary: {
          total: detail.total || detail.totalCount || detail.totalStudents,
          present:
            detail.present ||
            detail.presentCount ||
            detail.attendedCount,
          absent:
            detail.absent || detail.absentCount || detail.missingCount,
        },
        absentList: Array.isArray(absentList) ? absentList : [],
      });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "加载签到详情失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },
});
