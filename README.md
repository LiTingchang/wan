iOS--AnnyComics
===
分为5个工程

1.[Business]  (include Pod)
---
1. MVC
2. AppContext [继承自Service工程:用于管理单例]
3. Resrouces
4. Base : [业务层基类：baseView,baseViewController]

2.[UI]  (include Pod)
---
1. ThirdPartyUI or commonUI or CategoryUI
（TableView,MonitorWindow,Navigator,HudView,GridView,Photo,SegmentControl,CoreText）

3.[Service]  (include Core,Pod)
---
1. AppContext : [管理单例]
2. Config : 配置信息(version, mApi, secretKey, constant, SDKInfo)
3. Service : [DataLoader]
4. DAO : [DataModel]
5. Manager : [tool,userDefault,sinaManager,weixinManager]
6. SDK : [第三方静态库 sinaSDK,weixinSDK,CrashlyticsSDK]

4.[Core]  (include Pod)
---
1. M9Networking
2. PKRecord
3. Base Category

5.[Pod]  (pod install)
---
1. AFNetworking
2. TMCache
3. Masonry
4. DDLog
5. SDWebImage
6. FLEX


