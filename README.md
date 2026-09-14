# plugin-lightgallery

提供对 [lightgallery.js](https://github.com/sachinchoolur/lightgallery.js) 的集成，支持在内容页放大显示图片。

## 开发环境

```bash
git clone git@github.com:halo-sigs/plugin-lightgallery.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/plugin-lightgallery.git
```

```bash
cd path/to/plugin-lightgallery
```

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    classes-directories:
      - "build/classes"
      - "build/resources"
    lib-directories:
      - "libs"
    fixedPluginPath:
      - "/path/to/plugin-lightgallery"
```

## 使用方式

1. 在 [Releases](https://github.com/halo-sigs/plugin-lightgallery/releases) 下载最新的 JAR 文件。
2. 在 Halo 后台的插件管理上传 JAR 文件进行安装。
3. 进入 plugin-lightgallery 插件的设置页面，配置 `DOM 节点选择` 设置选项。

## 主题适配

此插件无需主题主动适配即可使用，其原理就是将 `lightgallery.js` 所需的依赖引入和初始化代码都自动插入到了内容页面上。因此，主题开发者无需再针对图片放大进行适配开发，如果有特殊的需求，建议共同完善此插件。

## lightGallery 2

图库升级为 lightGallery 2.9.0，缩放插件和样式随插件打包，无需主题额外加载资源。

本项目按 GPL-3.0 发布。lightGallery 2 的默认开发密钥会在浏览器控制台显示生产使用提醒；上游要求 GPLv3 兼容项目联系作者获取密钥，正式发布前需完成该事项。参见 [上游设置文档](https://www.lightgalleryjs.com/docs/settings/#licenseKey) 和 [授权说明](https://www.lightgalleryjs.com/license/)。
