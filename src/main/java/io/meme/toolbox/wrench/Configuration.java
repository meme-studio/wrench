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

    void includeInvisibleMethod() {
        visibility |= INVISIBLE_METHOD;
    }

    void includeInvisibleClass() {
        visibility |= INVISIBLE_CLASS;
    }

    void includeInvisibleField() {
        visibility |= INVISIBLE_FIELD;
    }

    void visible() {
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

    boolean isPackageIncluded(String className) {
        return matchesPackages(inclusionPackages, className);
    }

    boolean isPackageExcluded(String className) {
        return matchesPackages(exclusionPackages, className);
    }

    private boolean matchesPackages(List<String> packages, String className) {
        return packages.stream().anyMatch(className::contains);
    }


}
