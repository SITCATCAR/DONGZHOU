### 总结
- 数据库使用了Room，只写部分sql简单了很多
- 各类创建Activity分为了FromCreate和AppCreate两类(不好用，修改布局麻烦，不如分开写全部activity用include复用布局)
- 历史界面依然使用状态机，传参显示收藏和普通界面。收藏超过三条显示view all。证明了封装容易扩展。
- 创建界面和Text创建实现了粘贴板最后一条内容，点击加入输入框
- 扫码界面依然使用camerax实现了缩放和闪光灯，实现相册扫码失败弹窗
- 创建Text二维码字符串超过限制则用gzip压缩为base64扫码时解析
- dark模式使用了AppCompactDelegate提供的API
- 相机权限和相册取图片使用了ActivityContacts新API，暂时没有封装
- 权限拒绝后弹出再次申请弹窗，多次拒绝跳转appinfo界面同意权限。