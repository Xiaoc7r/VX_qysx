一、项目概述与全局技术规范
1.1 项目目标
本项目旨在开发一款支持多角色（教师与学生）的课堂签到微信小程序。系统需要实现完整的课程管理、定位签到、数据统计以及历史记录查询等功能，并且在技术实现上严格遵循特定的架构规范，以便于Agent能够直接依据本文档进行模块化、步骤化的代码生成（Coding）。
1.2 全局技术栈与框架版本约束
根据项目核心要求，前后端开发必须严格采用以下技术栈及版本：
- 开发语言：Java 17
- 核心框架：SpringBoot 3.5.10
- 持久层框架：MyBatis-Plus 3.5.15、mybatis-spring 3.0.3
- 数据库引擎：MySQL 8.0
- 前端框架：微信小程序原生开发（原生WXML/WXSS/JS）
- 接口规范：RESTful API 架构风格
- 安全鉴权：JWT (JSON Web Token) 令牌控制访问
  1.3 核心开发规范要求
  Agent在生成代码时，必须无条件遵循以下底层规范：
1. 数据库与实体类规范：
- 所有业务表强制包含 id、create_time、update_time 字段。
- id 字段主键必须采用 UUID 格式生成，禁止使用数据库自增主键。
- create_time 与 update_time 字段必须由系统层面（如 MyBatis-Plus 的 MetaObjectHandler）自动填充，严禁在业务逻辑中手动设值。
- 必须提取共有字段（id, create_time, update_time）形成公共实体父类 BaseEntity，所有具体业务实体类继承该父类。
2. 通用响应与异常规范：
- 统一封装全局接口返回结果类 Result<T>，包含状态码、提示信息与数据体。
- 结果状态类型强制采用枚举类型定义（如 ResultCodeEnum），禁止硬编码状态码。
- 实现统一的全局异常处理组件（@RestControllerAdvice），捕获系统级异常并封装自定义业务异常类（BusinessException）。
3. 日志与文件处理规范：
- 配置统一的日志管理机制（如利用AOP记录接口出入参、执行时间及异常堆栈）。
- 图片及文件上传至服务器本地后，必须通过配置 SpringBoot 静态资源映射代理实现直接访问，禁止每次上传后需要重启服务才能访问的问题。
4. 前端组件规范：
- 微信小程序端涉及相同或类似的UI与功能模块（如课程卡片、签到项目卡片），必须提取并封装为自定义组件（Custom Component）进行复用。
  二、数据库详细设计方案
  系统主要包含五个核心业务表，Agent生成建表语句及实体类需以此为准。
  2.1 用户信息表 (sys_user)
  存储教师与学生的基本账户信息。
- id (VARCHAR 32)：主键，UUID。
- username (VARCHAR 50)：登录账号，需设为唯一索引。
- password (VARCHAR 100)：加密后的密码。
- real_name (VARCHAR 50)：真实姓名。
- identifier (VARCHAR 50)：学号或工号。
- role (TINYINT)：角色标识，0代表学生，1代表教师。
- create_time (DATETIME)：创建时间。
- update_time (DATETIME)：更新时间。
  2.2 课程信息表 (course)
  存储教师创建的课程基础信息。
- id (VARCHAR 32)：主键，UUID。
- course_name (VARCHAR 100)：课程名称。
- course_desc (VARCHAR 255)：课程简介。
- teacher_id (VARCHAR 32)：创建该课程的教师ID，关联 sys_user.id。
- create_time (DATETIME)：创建时间。
- update_time (DATETIME)：更新时间。
  2.3 学生课程关联表 (user_course)
  维护学生加入课程的多对多关系。
- id (VARCHAR 32)：主键，UUID。
- student_id (VARCHAR 32)：学生ID，关联 sys_user.id。
- course_id (VARCHAR 32)：课程ID，关联 course.id。
- create_time (DATETIME)：加入时间。
- update_time (DATETIME)：更新时间。
  2.4 签到项目表 (sign_project)
  存储单次课堂签到任务的配置信息。
- id (VARCHAR 32)：主键，UUID。
- project_name (VARCHAR 100)：签到名称。
- course_id (VARCHAR 32)：所属课程ID。
- start_time (DATETIME)：签到开放时间。
- end_time (DATETIME)：签到截止时间。
- center_lat (DECIMAL 10,7)：教师设定的签到中心点纬度。
- center_lng (DECIMAL 10,7)：教师设定的签到中心点经度。
- radius (INT)：允许学生签到的有效半径误差（单位：米）。
- create_time (DATETIME)：创建时间。
- update_time (DATETIME)：更新时间。
  2.5 签到记录表 (sign_record)
  记录学生具体的签到行为与状态。
- id (VARCHAR 32)：主键，UUID。
- project_id (VARCHAR 32)：关联签到项目ID。
- student_id (VARCHAR 32)：签到学生ID。
- sign_time (DATETIME)：实际提交签到的时间。
- sign_lat (DECIMAL 10,7)：学生实际所在纬度。
- sign_lng (DECIMAL 10,7)：学生实际所在经度。
- status (TINYINT)：签到状态，0代表缺勤，1代表已签到。
- create_time (DATETIME)：创建时间。
- update_time (DATETIME)：更新时间。
  三、前后端核心业务流程交互设计
  根据核心流程设计图，系统分为三个主控Tab：首页、课程页、我的。
  3.1 账号鉴权流转
1. 用户进入小程序，若本地缓存无有效JWT，强制重定向至“登录/注册”页。
2. 学生端/教师端输入信息调用 /api/auth/login。
3. 后端校验通过后返回JWT，前端将令牌存入 wx.setStorageSync('token')。
4. 前端封装统一网络请求拦截器，在所有业务请求Header中自动附加 Authorization: Bearer <token>。
   3.2 首页 (近期签到模块)
1. 展示逻辑：首页默认请求近期签到接口。学生端展示其已加入课程中正在进行或刚结束的签到项目；教师端展示自己发布的近期签到项目。
2. 交互逻辑：
- 点击“查看更多”跳转至“签到列表页”（二级页面，含进行中与历史记录筛选）。
- 点击具体项目卡片，进入“签到详情页”。
- 签到详情页包含 进行中签到 和 历史记录。
- 学生端在详情页可见“点名签到”按钮，点击唤起微信地理位置API及Map组件。
  3.3 课程页 (课程管理模块)
1. 学生端视图：顶部包含“我的课程”与“全部课程”Tab。
- 全部课程：展示系统内所有教师开设的课程列表，卡片附带“加入”按钮。点击“加入”调用关联接口，成功后刷新列表。
- 我的课程：展示已绑定的课程列表，卡片附带“退课”按钮。
2. 教师端视图：仅展示自己创建的课程。点击进入课程详情后，可查看学生列表及本课程下的所有签到历史。
3. 课程详情页：作为二级页面，展示课程基本信息，并支持跳转至属于该课程的“签到记录”和“学生列表”。
   3.4 签到列表与详情页 (核心签到闭环)
1. 签到列表页：通过Tab切换“进行中”任务与“历史记录”任务。
2. 点名签到操作 (仅学生端)：
- 用户在签到详情页点击“点名签到”。
- 前端调用 wx.getLocation({ type: 'gcj02' }) 获取实时坐标。
- 前端将坐标、项目ID提交至后端 /api/sign/record/submit。
- 后端依据系统时间校验是否超出 end_time，利用 Haversine 公式计算当前坐标与 center_lat, center_lng 的物理距离，判断是否小于 radius。
- 全部校验通过，插入 sign_record 表，状态标记为已签到，返回成功响应。
  3.5 我的页面 (个人中心模块)
1. 顶部展示当前登录用户的头像、姓名、角色信息。
2. 提供快捷入口：“我的课程”、“签到历史”、“签到统计”、“创建课程（仅教师可见）”。
3. 底部提供“登出”按钮，点击后清除本地JWT缓存并重定向至登录页。
   四、后端RESTful API详细规范
   Agent在生成 Controller 与 Service 时，需严格实现以下接口契约。
   4.1 认证模块 API
- 登录接口
    - Method: POST /api/auth/login
    - Body: { "username": "xxx", "password": "xxx" }
    - Response: Result<String> (返回生成的JWT Token)
- 注册接口
    - Method: POST /api/auth/register
    - Body: { "username": "xxx", "password": "xxx", "real_name": "xxx", "identifier": "xxx", "role": 0 }
    - Response: Result<Void>
      4.2 课程模块 API
- 创建课程 (教师端)
    - Method: POST /api/course/create
    - Header: Authorization (后台解析获取教师ID)
    - Body: { "course_name": "软件工程", "course_desc": "必修" }
    - Response: Result<Void>
- 获取课程列表
    - Method: GET /api/course/list
    - Query: type (枚举值：all-全部, my-我的)
    - Response: Result<List<CourseDTO>>
- 加入课程 (学生端)
    - Method: POST /api/course/join/{courseId}
    - Response: Result<Void>
- 退出课程 (学生端)
    - Method: POST /api/course/leave/{courseId}
    - Response: Result<Void>
      4.3 签到核心 API
- 创建签到项目 (教师端)
    - Method: POST /api/sign/project/create
    - Body: { "course_id": "xxx", "project_name": "第十周签到", "start_time": "...", "end_time": "...", "center_lat": 39.9042, "center_lng": 116.4074, "radius": 200 }
    - Response: Result<Void>
- 获取近期签到项目
    - Method: GET /api/sign/project/recent
    - Response: Result<List<SignProjectDTO>> (返回与当前用户关联的按时间倒序的前N条记录)
- 获取签到项目列表
    - Method: GET /api/sign/project/list
    - Query: courseId (可选), status (ongoing/history)
    - Response: Result<List<SignProjectDTO>>
- 提交签到动作 (学生端)
    - Method: POST /api/sign/record/submit
    - Body: { "project_id": "xxx", "sign_lat": 39.9045, "sign_lng": 116.4071 }
    - Response: Result<Void> (后台拦截器捕获业务异常并返回距离过远、已超时等中文提示)
      4.4 统计与辅助 API
- 文件上传
    - Method: POST /api/file/upload
    - Body: multipart/form-data
    - Response: Result<String> (返回虚拟资源路径URL)
- 个人签到统计
    - Method: GET /api/sign/stat/personal
    - Response: Result<StatDTO> (包含应签总数、实签总数、缺勤数、出勤率)
      五、面向 Agent Coding 的分步开发指令序列
      为了保证项目按照规范顺利落地，Agent需按照以下5个阶段逐步生成代码。
      阶段一：基础设施与底层封装
1. 初始化 SpringBoot 3.5.10 项目，引入所需的 Web、MySQL、MyBatis-Plus、JWT、Lombok 等依赖。
2. 创建 Result 泛型响应类与 ResultCodeEnum 状态枚举。
3. 创建 BaseEntity 父类，配置 @TableId 及公共时间字段的 @TableField，实现 MetaObjectHandler 完成时间的自动填充。
4. 配置统一日志拦截切面（LogAspect）与全局异常处理器（GlobalExceptionHandler）。
5. 配置基于 WebMvcConfigurer 的本地文件上传路径到虚拟URL的映射规则。
   阶段二：用户认证与鉴权配置
1. 根据数据字典生成 sys_user 对应的实体、Mapper与Service。
2. 实现 JWT 工具类（生成Token与解析Token）。
3. 实现 AuthInterceptor，验证请求头的 Token，并将解析后的用户 ID 写入 ThreadLocal 上下文对象中。
4. 编写 LoginController，完成登录与注册接口逻辑。
5. 在小程序端编写网络请求工具类 request.js，实现Token的无感携带与401状态码统一跳转逻辑。
   阶段三：课程管理与前端组件开发
1. 生成 course 与 user_course 表相关的后端代码。
2. 开发课程列表查询、课程创建、加入与退出课程的具体业务逻辑接口。
3. 在小程序端创建 components/course-card 自定义组件，规范卡片的UI渲染。
4. 完成小程序“课程页”与“课程详情页”的页面搭建与接口对接。
   阶段四：签到业务与地图坐标算法
1. 生成 sign_project 与 sign_record 表的后端代码。
2. 编写签到项目发布接口。
3. 核心算法实现：在处理签到提交的Service中，利用Java实现根据两点经纬度计算球面距离的算法，校验其是否在设定 radius 内。
4. 开发按状态、关联课程筛选签到列表的后端接口。
   阶段五：小程序端签到闭环与数据展示
1. 在小程序端创建 components/sign-item 签到卡片组件。
2. 实现“首页”近期签到列表的渲染。
3. 开发“签到详情页”，引入 Map 组件展示中心点覆盖圈与用户当前定位。
4. 对接 wx.getLocation 并完成点名签到的全流程联调。
5. 开发“我的”页面，集成签到统计与历史记录接口的UI展示。