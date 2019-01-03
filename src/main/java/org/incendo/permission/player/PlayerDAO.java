package org.incendo.permission.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.incendo.permission.Group;
import org.incendo.permission.Permission;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class PlayerDAO {

    private final Collection<Group> groups;
    private final Collection<Permission> permissions; // player specific permissions
    private final Map<String, String> properties;

    public static final PlayerDAO EMPTY = new PlayerDAO(Collections.emptyList(),
        Collections.emptyList(), Collections.emptyMap());

}
