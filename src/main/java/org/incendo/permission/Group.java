package org.incendo.permission;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
public final class Group {

    private final String name;
    private final Group parent;
    private final Collection<Consumer<Group>> updateSubscribers = Lists.newArrayList();

    private final Collection<Permission> permissions = new ArrayList(); // initial permissions
    private final Collection<Permission> effectivePermissions = new HashSet<>();

    private boolean modified = true;

    public boolean hasParent() {
        return this.parent != null;
    }

    public void registerUpdateSubscriber(@NotNull final Consumer<Group> subscriber) {
        Preconditions.checkNotNull(subscriber);
        this.updateSubscribers.add(subscriber);
    }

    public void addPermission(@NotNull final Permission permission) {
        Preconditions.checkNotNull(permission);
        this.removePermission(permission); // make sure to remove any one similar permission before
        this.permissions.add(permission);
        this.setModified();
        this.update();
    }

    public void removePermission(@NotNull final Permission permission) {
        Preconditions.checkNotNull(permission);
        getPermissionByName(permission.getRawName()).ifPresent(effectivePermission -> {
            this.permissions.remove(effectivePermission);
            setModified();
            update();
        });
    }

    private void setModified() {
        this.modified = true;
    }

    private void update() {
        if (this.modified) {
            this.getEffectivePermissions();
            this.updateSubscribers.forEach(consumer -> consumer.accept(this));
        }
    }

    @NotNull public Optional<Permission> getPermissionByName(@NotNull final String name) {
        for (final Permission permission : this.getPermissions()) {
            if (permission.getRawName().equalsIgnoreCase(name)) {
                return Optional.of(permission);
            }
        }
        return Optional.empty();
    }

    public Collection<Permission> getEffectivePermissions() {
        if (modified) {
            final Map<String, Permission> priorityPermissions = new HashMap<>();
            final Set<Group> checked = new HashSet<>();
            for (;;) {
                Group tempGroup = this;
                // find highest order parent that hasn't been checked
                while (tempGroup.hasParent()) {
                    final Group parent = tempGroup.getParent();
                    if (checked.contains(parent)) {
                        break;
                    } else {
                        tempGroup = parent;
                    }
                }
                // check all group permissions
                for (final Permission permission : tempGroup.getPermissions()) {
                    if (priorityPermissions.containsKey(permission.getRawName())) {
                        final Permission priorityPermission = priorityPermissions.get(permission.getRawName());
                        // if the previously stored permission is a priority permission, and this one isn't
                        // then the old one will remain
                        if (priorityPermission.isPriority() && !permission.isPriority()) {
                            continue;
                        }
                    }
                    priorityPermissions.put(permission.getRawName(), permission);
                }
                checked.add(tempGroup);
                // if this is true, then we've checked all effective groups
                if (tempGroup == this) {
                    break;
                }
            }
            this.effectivePermissions.clear();
            this.effectivePermissions.addAll(priorityPermissions.values());
            this.modified = false;
        }
        return Collections.unmodifiableCollection(this.effectivePermissions);
    }

}
