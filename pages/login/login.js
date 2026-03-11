const request = require("../../utils/request");

function decodeBase64Url(input) {
  const normalized = input.replace(/-/g, "+").replace(/_/g, "/");
  const padded = normalized.padEnd(
    normalized.length + ((4 - (normalized.length % 4)) % 4),
    "="
  );
  const chars =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  let str = "";
  let buffer = 0;
  let bits = 0;
  for (let i = 0; i < padded.length; i += 1) {
    const ch = padded.charAt(i);
    if (ch === "=") {
      break;
    }
    const value = chars.indexOf(ch);
    if (value === -1) {
      continue;
    }
    buffer = (buffer << 6) | value;
    bits += 6;
    if (bits >= 8) {
      bits -= 8;
      str += String.fromCharCode((buffer >> bits) & 0xff);
    }
  }
  return str;
}

function parseTokenPayload(token) {
  if (!token || typeof token !== "string") {
    return null;
  }
  const parts = token.split(".");
  if (parts.length < 2) {
    return null;
  }
  try {
    const payload = decodeBase64Url(parts[1]);
    return JSON.parse(payload);
  } catch (err) {
    return null;
  }
}

Page({
  data: {
    username: "",
    password: "",
    loading: false,
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  async onSubmit() {
    const { username, password, loading } = this.data;

    if (loading) {
      return;
    }

    if (!username || !password) {
      wx.showToast({ title: "请填写用户名和密码", icon: "none" });
      return;
    }

    this.setData({ loading: true });
    try {
      const res = await request({
        url: "/auth/login",
        method: "POST",
        data: { username, password },
      });
      const data = res.data && res.data.data;
      const token = data && data.token ? data.token : data;
      if (!token) {
        wx.showToast({ title: "未获取到 Token", icon: "none" });
        return;
      }
      wx.setStorageSync("token", token);
      let userId = data && data.userId;
      let userType = data && data.userType;
      if (!userId || !userType) {
        const payload = parseTokenPayload(token);
        userId =
          userId ||
          (payload && (payload.userId || payload.user_id || payload.sub));
        userType =
          userType ||
          (payload && (payload.userType || payload.user_type || payload.role));
      }
      if (userId) {
        wx.setStorageSync("userId", userId);
      }
      if (userType || userType === 0) {
        const normalizedType = Number(userType);
        wx.setStorageSync(
          "userType",
          Number.isNaN(normalizedType) ? userType : normalizedType
        );
      }
      wx.reLaunch({ url: "/pages/index/index" });
    } catch (err) {
      const message =
        err && err.data && err.data.message
          ? err.data.message
          : "登录失败";
      wx.showToast({ title: message, icon: "none" });
    } finally {
      this.setData({ loading: false });
    }
  },

  onGoRegister() {
    wx.navigateTo({ url: "/pages/register/register" });
  },
});
