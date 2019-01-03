## List of commands
Arguments: **\<arg\>** is required, **\[arg\]** is optional.

### Base
- /perms (incendoperms.use)
- /perms group (incendoperms.use.group)

#### Group Commands
- /perms group create \<group-name\> \[parent\] (incendoperms.use.group.create)
- /perms group add \<group-name\> \<permission-node\> \[...flags\] (incendoperms.use.group.add)
- /perms group remove \<group-name\>  \<permission-node\> (incendoperms.use.group.remove)
- /perms group tree \<group-name\> (incendoperms.use.group.tree)
- /perms group list \<group-name\> (incendoperms.use.group.list)
- /perms group set \<group-name\> \<property-name\> \<property-value\> (incendoperms.use.group.set)

#### Player Commands
- /perms player add \<player-name\> \<group/perm\> \<name\> \[...flags\] (incendoperms.use.player.add)
- /perms player remove \<player-name\> \<group/perm\> \<name\> (incendoperms.use.player.remove)
- /perms player list \<player-name\> (incendoperms.use.player.list)
- /perms player set \<player-name\> \<property-name\> \<property-value\> (incendoperms.use.player.set)

#### General Commands
- /perms test player/group \<name\> \<permission\> (incendoperms.use.test)
