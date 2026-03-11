const baseURL = "http://127.0.0.1:8080";
const SUCCESS_CODE = 200;

function request(options) {
  const {
    url,
    method = "GET",
    data,
    header = {},
  } = options || {};

  const token = wx.getStorageSync("token");
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

  return new Promise((resolve, reject) => {
    if (!url) {
      reject(new Error("request: url is required"));
      return;
    }

    wx.request({
      url: `${baseURL}${url}`,
      method,
      data,
      header: {
        "Content-Type": "application/json",
        ...authHeader,
        ...header,
      },
      success(res) {
        if (res.statusCode === 401) {
          wx.clearStorageSync();
          wx.reLaunch({ url: "/pages/login/login" });
          reject(res);
          return;
        }

        if (res.statusCode >= 200 && res.statusCode < 300) {
          const code = res.data && res.data.code;
          if (typeof code !== "undefined" && code !== SUCCESS_CODE) {
            const message =
              (res.data && res.data.message) || "请求失败";
            wx.showToast({ title: message, icon: "none" });
            reject(res);
            return;
          }
          resolve(res);
          return;
        }

        reject(res);
      },
      fail(err) {
        reject(err);
      },
    });
  });
}

module.exports = request;
