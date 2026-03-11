const request = require("../../../utils/request");

const STATUS_LABELS = {
  normal: "正常",
  success: "正常",
  ok: "正常",
  distance: "距离过远",
  far: "距离过远",
  miss: "未签到",
  absent: "未签到",
};

Page({
  data: {
    recordId: null,
    detail: null,
    statusLabel: "",
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
        url: `/attendance/student/detail/${this.data.recordId}`,
        method: "GET",
      });
      const detail = res.data && res.data.data ? res.data.data : null;
      this.setData({
        detail,
        statusLabel: this.formatStatus(detail),
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

  formatStatus(detail) {
    if (!detail) {
      return "";
    }
    const raw =
      detail.status || detail.result || detail.sign_status || "";
    const key = typeof raw === "string" ? raw.toLowerCase() : raw;
    if (key === 1) {
      return "正常";
    }
    if (key === 2) {
      return "距离过远";
    }
    if (key === 0) {
      return "未签到";
    }
    return STATUS_LABELS[key] || `${raw}`;
  },
});
