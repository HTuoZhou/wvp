#### 目录结构说明

##### wvp

- **wvp-bootstrap：**

  启动模块，负责项目启动

- **wvp-common：**

  通用模块，提供通用的配置和工具

- **wvp-persistence：**

  持久层模块，负责数据库和缓存接入

- **wvp-business：**

  服务层模块，负责相关api层的业务实现

- **wvp-webapi：**

  自身系统api模块，提供给自身系统（web、移动端等）进行使用

- **wvp-api：**

  内部其它系统api模块，通过openFeign的方式提供给内部其它系统进行使用

- **wvp-openapi：**

  外部系统api模块，提供给外部系统（其它公司）进行使用