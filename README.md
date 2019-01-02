# IncendoPermissions
IncendoPermissions is an upcoming Minecraft permission plugin, aimed to be both lightweight and easy
to use, while still providing all the features expected of a permission plugin. IncendoPermissions aims
to achieve this in a sane way, without bloating the plugin with a bunch of features that most people
don't actually need or use.

### Features

#### The plugin will offer the following features:

**Permissions:**
- `*` wildcard support, etc: `your.permission.group.*`. Matches all permissions of the type `your.permission.group.<perm...>`
- `!` priority permission, will override all permissions from higher priority groups. Permission checks are greedy, and a positive  permission
  node with a priority prefix will always return true
- `-` permission negation. Prefixing a permission with the negation prefix, will forcefully revert the permission. This can be combined with
  the priority prefix
  
**Groups:**
Groups are hierarchical. If a permission group has a parent, then it will inherit the parent permissions. Child permissions will
always override parent permissions, unless the parent permission is a priority permission.

An example setup would be:

member\
├── builder\
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└── advanced builder\
└── moderator\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└── administrator
    
A player may belong to several permission groups, in which case a greedy search will be performed for any positive permission node. 
Players may also have personal permission nodes assigned.

**Filters:**
Permission nodes can be defined per world and gamemode, and they may also be timed. There will be
an API that will allow you to extend this.

**Properties:**
Groups and players can be assigned properties, such as chat prefixes and suffixes. There will be an
API that will allow you to extend this.

### Code Style

If you are planning to commit any changes to the project,
it would be highly appreciated if you were to follow the 
project code style conventions. To make this easier we have
provided settings that can be imported into your IDE.

**Eclipse:**
`Window > Preferences > Java > Code Style > Formatter`
Press `Import` and select `...path/to/project/code_style.xml`

**IntelliJ:**
`File > Settings > Editor > Code Style`. Next to "Scheme" there is a cog wheel, press that and then
`Import Scheme > IntelliJ IDEA Code Style XML` and then select `..path/to/project/code_style.xml`
