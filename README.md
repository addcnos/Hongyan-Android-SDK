## 数睿鸿雁SDK Android文档
![LICENSE](https://img.shields.io/badge/License-MIT-orange)
![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Stable](https://img.shields.io/badge/Stable-v1.0.5-brightgreen.svg)

#### SDK概述
鸿雁即时通讯是数睿科技公司旗下的一款专注于为开发者提供实时聊天技术和服务的产品。我们的团队来自数睿科技，致力于为用户提供高效稳定的实时聊天云服务，且弹性可扩展，对外提供较为简洁的API接口，让您轻松实现快速集成即时通讯功能。

#### 环境依赖

SDK 兼容 Android 4.0+

## 集成流程

#### 1.引入SDK
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    implementation 'com.github.addcnos:Hongyan-Android-SDK:Tag'
}
```

#### 2.初始化SDK

```dart
// 初始化SDK
IMApp.init(this)
// 设置websocket域名
IMApp.setWSSHost("")
// 设置websocket-debug域名
IMApp.setWSSHostDebug("")
// 设置IM接口域名
IMApp.setIMHost("")
// 设置IM-debug接口域名
IMApp.setIMHostDebug("")
// 设置是否开启日志输出
IMApp.setLogEnabled(true)
// 设置是否开启接口debug模式
IMApp.setDebugApi(true)
// 进行连接，参数一：聊天token；参数二：是否重连
IMClient.getInstance().connect(token, reconnect)
// 设置通知栏消息接收监听
IMClient.getInstance().setOnReceiveNotifyListener(IMReceiveNotify())
```

#### 3.消息接收监听

```dart
// 注册消息接收监听器
IMClient.getInstance().setOnReceiveChatListener(this)
// 实现消息接收的方法
override fun onReceived(chatMessage: ChatMessage?) {
    // Implement a message accept callback function that handles its own business logic in this method
}
```

## 方法说明

#### 1.发送消息

```dart
//发送消息
sendMessage(token: String, type: String, targetUid: String, content: String, resultListener: OnResultCallbackListener)
```
| 参数        | 类型    |  说明  |
| --------   | -----:  | :----: |
| token          | String        |   聊天token    |
| type           | String        |   消息的类型   |
| targetUid      | String        |   聊天目标id   |
| content        | String        |   消息的内容   |
| resultListener | OnResultCallbackListener | 发送请求的结果回调监听 |


#### 2.获取历史消息

```dart
//拉取历史消息
loadHistoryData(token: String, targetUid: String, lastId: String, resultListener: OnResultCallbackListener)
```
| 参数        | 类型    |  说明  |
| --------    | -----:  | :----: |
| token       | String        |   聊天token      |
| targetUid   | String        |   聊天目标id     |
| lastId      | String        |   最后一条消息id |
| resultListener | OnResultCallbackListener | 发送请求的结果回调监听 |


#### 3.发送消息已读

```dart
//发送消息已读
readMsg(token: String, targetUid: String, resultListener: OnResultCallbackListener)
```
| 参数        | 类型    |  说明  |
| --------    | -----:  | :----: |
| token       | String        |   聊天token      |
| targetUid   | String        |   聊天目标id     |
| resultListener | OnResultCallbackListener | 发送请求的结果回调监听 |


#### 4.获取未读消息数

```dart
//获取所有的新消息数
getAllNewMessage(token: String, targetUid: String, resultListener: OnResultCallbackListener)
```
| 参数        | 类型    |  说明  |
| --------    | -----:  | :----: |
| token       | String        |   聊天token      |
| targetUid   | String        |   聊天目标id     |
| resultListener | OnResultCallbackListener | 发送请求的结果回调监听 |


#### 5.获取会员信息

```dart
//获取会员信息
getUserInfo(token: String, targetUid: String, resultListener: OnResultCallbackListener)
```
| 参数        | 类型    |  说明  |
| --------    | -----:  | :----: |
| token       | String        |   聊天token      |
| targetUid   | String        |   聊天目标id     |
| resultListener | OnResultCallbackListener | 发送请求的结果回调监听 |


#### 6.删除联络人

```dart
//删除联络人
deleteConversation(token: String, targetUid: String, resultListener: OnResultCallbackListener)
```
| 参数        | 类型    |  说明  |
| --------    | -----:  | :----: |
| token       | String        |   聊天token      |
| targetUid   | String        |   聊天目标id     |
| resultListener | OnResultCallbackListener | 发送请求的结果回调监听 |


#### 7.发送图片

```dart
//上传图片
uploadImage(token: String, file: File, resultListener: OnProgressListener)
```
| 参数        | 类型    |  说明  |
| --------    | -----:  | :----: |
| token       | String  |   聊天token      |
| file        | File    |   上传的图片文件对象     |
| resultListener | OnProgressListener | 上传图片的进度和结果回调监听 |


## 集成即时通讯UI（可选）

#### 1.集成聊天界面

```dart
// 继承聊天界面
public class ChatActivity : BaseChatActivity() {
    
    override fun onClickMessage(message: ChatMessage) {
        // 可以在此处实现点击消息的业务逻辑
         super.onClickMessage(message)
    }

    override fun onLongClickMessage(message: ChatMessage) {
        // 可以在此处实现长按点击消息的业务逻辑
         super.onLongClickMessage(message)
    }

    override fun onSendUrl(): String? {
        // 支持重写onSendUrl方法来实现自定义发送接口
        return "https://"
    }

    override fun onSendParam(): HashMap<String, String>? {
        // 可以通过重写onSendParam方法来实现自定义发送接口的参数
        return null
    }

    override fun onSendTextMessage(result: String): Boolean {
        // 可以通过重写onSendTextMessage方法来实现触发文本消息的发送(也可以用于接收文本框内容)
        return super.onSendTextMessage(result)
    }

    override fun onSendImageMessage(list: MutableList<String>): Boolean {
        // 可以通过重写onSendImageMessage方法来实现触发图片消息的发送(也可以用于接收图片路径集合)
        return super.onSendImageMessage(list)
    }

    override fun onReceived(chatMessage: ChatMessage?) {
        super.onReceived(chatMessage)
        // 可以通过重写实现消息接收的其他业务逻辑
    }

    override fun onOpen(httpStatus: Short?, httpStatusMessage: String?) {
        super.onOpen(httpStatus, httpStatusMessage)
        // 可以在此处实现连接成功的其他业务逻辑
    }

    override fun onError(ex: Exception) {
        super.onError(ex)
        // 可以在此处实现连接错误的其他业务逻辑
    }

    override fun onIMRequestCode(requestType: String, code: String) {
        super.onIMRequestCode(requestType, code)
        // 可以通过重写获取接口请求码，处理业务逻辑判断
    }

}
```

#### 2.集成消息列表界面


```dart
// 继承聊天列表界面
public class ChatListActivity extends BaseChatListActivity {

    @Override
    protected void setAdapterItemClick(String target_uid, String target_name, String token, int position) {
        // 点击聊天列表item，跳转到聊天界面
        Bundle bundle = new Bundle();
        bundle.putString("target_uid", target_uid);
        bundle.putString("target_name", target_name);
        bundle.putString("token", token);
        Intent intent = new Intent();
        intent.setClass(ChatListActivity.this, ChatActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected String initSetDeleteIm() {
        return "这里填长按删除url";
    }

    @Override
    protected String initListData() {
        return "这里填获取IM列表数据的url请求";
    }
}
```

## 版本更新说明

#### v1.0.5 版本（新）

更新日期：2021年2月1日<br>
1.集成UI库：支持一键接入聊天列表和聊天会话界面<br>

#### v1.0.4 版本

更新日期：2020年7月1日<br>
1.提供WS连接回调<br>

#### v1.0.3 版本

更新日期：2020年1月6日<br>
1.支持UI库简单控件的调用<br>
2.域名支持动态传入<br>

#### v1.0.2 版本

更新日期：2019年12月19日<br>
1.IM-SDK代码功能优化<br>

#### v1.0.1 版本

更新日期：2019年11月26日<br>
1.IM-SDK代码功能优化<br>

#### v1.0.0 版本

更新日期：2019年11月8日<br>
1.支持IM-SDK基础功能<br>


## 相关资料

#### [数睿鸿雁后端服务文档](https://github.com/addcnos/Hongyan-Server)
#### [数睿鸿雁SDK-flutter文档](https://github.com/addcnos/Hongyan-Flutter-SDK)
#### [数睿鸿雁SDK-Android文档](https://github.com/addcnos/Hongyan-Android-SDK)
#### [数睿鸿雁SDK-Objective-C文档](https://github.com/addcnos/Hongyan-IOS-SDK)
#### [数睿鸿雁SDK-Web文档](https://github.com/addcnos/Hongyan-Web-SDK)
