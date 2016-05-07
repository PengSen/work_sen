# work_sen
基于Jsoup的网络数据爬取完整项目
——完成于14年底，开发环境Eclipse。
（当时在学习状态，很多基础注释，希望能帮助到大家，谢谢浏览）

[从ip138爬取的四个功能：](http://www.ip138.com/)

1.天气预报-预报五天2.手机号码所在地区查询3.全国各地车牌查询表4.身份证号码查询验证
  
![首页](https://github.com/PengSen/work_sen/blob/master/Image/image_view.png)

主界面是可旋转的选择器，网上参考的代码。

####本地数据存储：
把一些不经常变动的数据存入了数据库，节省一定流量，避免每次抓取数据都需要去爬整个网页。比如全国地名天气的url（可以从Menu键选择网上爬取更新，也可用本地文件已写数据库）、已查的手机号数据。

####网络相关：
没有使用框架，直接用的HttpURLConnection请求网络数据。

####基本样式：
![选择器](https://github.com/PengSen/work_sen/blob/master/Image/image_select.png)

现在菜单键已经在淘汰边缘了~：
![菜单键](https://github.com/PengSen/work_sen/blob/master/Image/image_menu.png) 

有基于百度的定位，可以上滑查看本城市的天气预报：
![天气预报](https://github.com/PengSen/work_sen/blob/master/Image/image_weather.png) 

