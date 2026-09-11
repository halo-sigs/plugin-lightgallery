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
3. 进入插件设置，在「页面匹配规则」中添加需要启用灯箱的页面路径和图片所在区域。

### 页面匹配规则

每条规则由两部分组成：

- **路径匹配**：使用 Spring `PathPattern` 语法，不是正则表达式。填写页面路径，例如 `/moments`，不要填写域名、查询参数或 `#` 后的片段。语法参考 [PathPattern 文档](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/pattern/PathPattern.html)。
- **匹配区域**：使用 CSS 选择器，例如 `#content` 或 `.markdown-body`，插件会为匹配区域内的图片启用灯箱。同一选择器可以匹配多个区域。

匹配区域留空表示整个页面，Logo 等导航图片也会被包含。通常应填写主题实际使用的正文区域选择器；不同主题的正文结构可能不同。

「内容页面匹配」是旧版设置，仅为兼容保留，建议新配置使用「页面匹配规则」。

## 主题适配

此插件无需主题主动适配即可使用，其原理就是将 `lightgallery.js` 所需的依赖引入和初始化代码都自动插入到了内容页面上。因此，主题开发者无需再针对图片放大进行适配开发，如果有特殊的需求，建议共同完善此插件。
