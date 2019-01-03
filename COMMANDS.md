## List of commands
Arguments: **\<arg\>** is required, **\[arg\]** is optional.

### Base
- /perms (incendoperms.use)
- /perms group (incendoperms.use.group)

#### Group Commands
- /perms group create \<group-name\> \[parent\] (incendoperms.use.group.create)
- /perms group add \<group-name\> \<permission-node\> \[...flags\] (incendoperms.use.group.add)
- /perms group remove \<group-name\>  \<permission-node\>
- /perms group tree \<group-name\>
- /perms group list \<group-name\>
- /perms group set \<group-name\> \<property-name\> \<property-value\>

#### Player Commands
- /perms player add \<player-name\> \<group/perm\> \<name\> \[...flags\]
- /perms player remove \<player-name\> \<group/perm\> \<name\>
- /perms player list \<player-name\>
- /perms player set \<player-name\> \<property-name\> \<property-value\>

#### General Commands
- /perms test player/group \<name\> \<permission\>
