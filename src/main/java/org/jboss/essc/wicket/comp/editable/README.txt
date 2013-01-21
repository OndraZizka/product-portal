
    Editable label and link.

    They have two states:

      * Active  - editation enabled, behaves like an input box.
      * Passive - editation disabled, behaves like a label or link.

    EditableLink can get activated by:
      * External call to activate().
      * Shift + click. DONE: it was caused by user-select.

    EditableLabel can get enabled simply by clicking on it.

    Both can be passivated by:
     * Pressing Enter - leaves the new value.
     * Pressing Escape - reverts to original value.
     * Removing focus - by clicking elsewhere or pressing Tab.

    The "disabled" property is not used as it blocks all events in Firefox.
    The "readOnly" property is used when passivated.
    The "user-select" CSS property is not used, it would prevent Home/End.
    Escape needs to be caught in "onkeyup", otherwise value change would be overriden.

    Known issues:
     * Home/End don't work in EditableLink.

    Resources:
     * Key events demo: http://www.javascripter.net/faq/keyboardeventproperties.htm
     * MartinG gave me this as example of AJAXifying:
       Behavior: https://github.com/wicketstuff/core/blob/master/jdk-1.6-parent/autocomplete-tagit-parent/autocomplete-tagit/src/main/java/org/wicketstuff/tagit/TagItAjaxBehavior.java
       JS:       https://github.com/wicketstuff/core/blob/master/jdk-1.6-parent/autocomplete-tagit-parent/autocomplete-tagit/src/main/resources/org/wicketstuff/tagit/res/tag-it.tmpl.js


    @author Ondrej Zizka