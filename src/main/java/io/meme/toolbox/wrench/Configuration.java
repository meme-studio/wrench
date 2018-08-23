package io.meme.toolbox.wrench;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * @author meme
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor(staticName = "preset")
public class Configuration {

    private static final int INVISIBLE = 0;

    private static final int INVISIBLE_CLASS = 1;

    private static final int INVISIBLE_FIELD = 2;

    private static final int INVISIBLE_METHOD = 4;

    public static final int VISIBLE = INVISIBLE_CLASS | INVISIBLE_FIELD | INVISIBLE_METHOD;

    private int visibility = INVISIBLE;
    private List<String> inclusionPackages = singletonList("");
    private List<String> exclusionPackages = emptyList();

    public void includeInvisibleMethod() {
        visibility |= INVISIBLE_METHOD;
    }

    public void includeInvisibleClass() {
        visibility |= INVISIBLE_CLASS;
    }

    public void includeInvisibleField() {
        visibility |= INVISIBLE_FIELD;
    }

    public void visible() {
        visibility |= VISIBLE;
    }

    public boolean isEnableVisibleClass() {
        return (INVISIBLE_CLASS & visibility) > 0;
    }

    public boolean isEnableVisibleField() {
        return (INVISIBLE_FIELD & visibility) > 0;
    }

    public boolean isEnableVisibleMethod() {
        return (INVISIBLE_METHOD & visibility) > 0;
    }

    public boolean isPackageIncluded(String packageName) {
        return matchesPackages(inclusionPackages, packageName);
    }

    public boolean isPackageExcluded(String packageName) {
        return matchesPackages(exclusionPackages, packageName);
    }

    private boolean matchesPackages(List<String> packages, String packageName) {
        return packages.stream().anyMatch(packageName::contains);
    }


}
