const request = require("../../utils/request");

const ROLE_OPTIONS = [
  { label: "学生", value: 1 },
  { label: "教师", value: 2 },
];

Page({
  data: {
    username: "",
    password: "",
    realName: "",
    identifier: "",
    roleIndex: 0,
    loading: false,
  },

  onInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({ [field]: e.detail.value });
  },

  onRoleChange(e) {
    this.setData({ roleIndex: Number(e.detail.value) || 0 });
  },

  async onSubmit() {
    const {
      username,
      password,
      realName,
      identifier,
      roleIndex,
      loading,
    } = this.data;

    if (loading) {
      return;
    }

    if (!username || !password || !realName || !identifier) {
      wx.showToast({ title: "请完整填写注册信息", icon: "none" });
      return;
    }

    this.setData({ loading: true });
    try {
      const role = ROLE_OPTIONS[roleIndex].value;
      await request({
        url: "/auth/register",
        method: "POST",
        data: {
          username,
          password,
          real_name: realName,
          identifier,
          role,
        },
      });
      wx.showToast({ title: "注册成功", icon: "success" });
      wx.navigateBack();
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "注册失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },
});
