/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.text;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Represents an immutable instance of formatted text that can be displayed on
 * the client. Each instance consists of content and a list of children texts
 * appended after the content of this text. The content of the text is available
 * through one of the subclasses.
 *
 * <p>Text is primarily used for sending formatted chat messages to players, but
 * also in other places like books or signs.</p>
 *
 * <p>Text instances can be either directly created through the available
 * constructor or using the {@link Builder} available through one of the
 * {@link Texts#builder()} methods, which is the recommended way.</p>
 *
 * @see Texts#builder()
 * @see Builder
 * @see Literal
 * @see Translatable
 * @see Selector
 * @see Score
 */
public abstract class Text implements TextRepresentable {

    protected final TextFormat format;
    protected final ImmutableList<Text> children;
    protected final Optional<ClickAction<?>> clickAction;
    protected final Optional<HoverAction<?>> hoverAction;
    protected final Optional<ShiftClickAction<?>> shiftClickAction;

    /**
     * An {@link Iterable} providing an {@link Iterator} over this {@link Text}
     * as well as all children text and their children.
     */
    protected final Iterable<Text> childrenIterable = new Iterable<Text>() {

        @Override
        public Iterator<Text> iterator() {
            return Text.this.children.isEmpty() ? Iterators.singletonIterator(Text.this) : new TextIterator(Text.this);
        }

    };

    Text() {
        this(new TextFormat(), ImmutableList.<Text>of(), null, null, null);
    }

    /**
     * Constructs a new immutable {@link Text} with the specified formatting and
     * text actions applied.
     *
     * @param format The format of the text
     * @param children The immutable list of children of the text
     * @param clickAction The click action of the text, or {@code null} for none
     * @param hoverAction The hover action of the text, or {@code null} for none
     * @param shiftClickAction The shift click action of the text, or
     *        {@code null} for none
     */
    Text(TextFormat format, ImmutableList<Text> children, @Nullable ClickAction<?> clickAction,
            @Nullable HoverAction<?> hoverAction, @Nullable ShiftClickAction<?> shiftClickAction) {
        this.format = checkNotNull(format, "format");
        this.children = checkNotNull(children, "children");
        this.clickAction = Optional.<ClickAction<?>>ofNullable(clickAction);
        this.hoverAction = Optional.<HoverAction<?>>ofNullable(hoverAction);
        this.shiftClickAction = Optional.<ShiftClickAction<?>>ofNullable(shiftClickAction);
    }

    /**
     * Returns the format of this {@link Text}.
     *
     * @return The format of this text
     */
    public final TextFormat getFormat() {
        return this.format;
    }

    /**
     * Returns the color of this {@link Text}.
     *
     * @return The color of this text
     */
    public final TextColor getColor() {
        return this.format.getColor();
    }

    /**
     * Returns the style of this {@link Text}. This will return a compound
     * {@link TextStyle} if multiple different styles have been set.
     *
     * @return The style of this text
     */
    public final TextStyle getStyle() {
        return this.format.getStyle();
    }

    /**
     * Returns the immutable list of children appended after the content of this
     * {@link Text}.
     *
     * @return The immutable list of children
     */
    public final ImmutableList<Text> getChildren() {
        return this.children;
    }

    /**
     * Returns an immutable {@link Iterable} over this text and all of its
     * children. This is recursive, the children of the children will be also
     * included.
     *
     * @return An iterable over this text and the children texts
     */
    public final Iterable<Text> withChildren() {
        return this.childrenIterable;
    }

    /**
     * Returns the {@link ClickAction} executed on the client when this
     * {@link Text} gets clicked.
     *
     * @return The click action of this text, or {@link Optional#empty()} if
     *         not set
     */
    public final Optional<ClickAction<?>> getClickAction() {
        return this.clickAction;
    }

    /**
     * Returns the {@link HoverAction} executed on the client when this
     * {@link Text} gets hovered.
     *
     * @return The hover action of this text, or {@link Optional#empty()} if
     *         not set
     */
    public final Optional<HoverAction<?>> getHoverAction() {
        return this.hoverAction;
    }

    /**
     * Returns the {@link ShiftClickAction} executed on the client when this
     * {@link Text} gets shift-clicked.
     *
     * @return The shift-click action of this text, or {@link Optional#empty()}
     *         if not set
     */
    public final Optional<ShiftClickAction<?>> getShiftClickAction() {
        return this.shiftClickAction;
    }

    /**
     * Returns a new {@link Builder} with the content, formatting and
     * actions of this text. This can be used to edit an otherwise immutable
     * {@link Text} instance.
     *
     * @return A new message builder with the content of this text
     */
    public abstract Builder builder();

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Text)) {
            return false;
        }

        Text that = (Text) o;
        return this.format.equals(that.format)
                && this.children.equals(that.children)
                && this.clickAction.equals(that.clickAction)
                && this.hoverAction.equals(that.hoverAction)
                && this.shiftClickAction.equals(that.shiftClickAction);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.format, this.children, this.clickAction, this.hoverAction, this.shiftClickAction);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(Text.class)
                .add("format", this.format)
                .add("children", this.children)
                .add("clickAction", this.clickAction)
                .add("hoverAction", this.hoverAction)
                .add("shiftClickAction", this.shiftClickAction)
                .toString();
    }

    @Override
    public final Text toText() {
        return this;
    }

    /**
     * Represents a {@link Text} containing a plain text {@link String}.
     *
     * @see Builder
     */
    public static class Literal extends Text {

        protected final String content;

        Literal() {
            this("");
        }

        Literal(String content) {
            this.content = checkNotNull(content, "content");
        }

        /**
         * Constructs a new immutable {@link Literal} for the given plain text
         * content with the specified formatting and text actions applied.
         *
         * @param format The format of the text
         * @param children The immutable list of children of the text
         * @param clickAction The click action of the text, or {@code null} for
         *        none
         * @param hoverAction The hover action of the text, or {@code null} for
         *        none
         * @param shiftClickAction The shift click action of the text, or
         *        {@code null} for none
         * @param content The plain text content of the text
         */
        Literal(TextFormat format, ImmutableList<Text> children, @Nullable ClickAction<?> clickAction,
                @Nullable HoverAction<?> hoverAction, @Nullable ShiftClickAction<?> shiftClickAction, String content) {
            super(format, children, clickAction, hoverAction, shiftClickAction);
            this.content = checkNotNull(content, "content");
        }

        /**
         * Returns the plain text content of this {@link Text}.
         *
         * @return The content of this text
         */
        public final String getContent() {
            return this.content;
        }

        @Override
        public Builder builder() {
            return new Builder(this);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Literal) || !super.equals(o)) {
                return false;
            }

            Literal that = (Literal) o;
            return this.content.equals(that.content);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), this.content);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .addValue(super.toString())
                    .add("content", this.content)
                    .toString();
        }

        /**
         * Represents a {@link Text.Builder} creating immutable {@link Literal}
         * instances.
         *
         * @see Literal
         */
        public static class Builder extends Text.Builder {

            protected String content;

            /**
             * Constructs a new empty {@link Builder}.
             */
            Builder() {
                this("");
            }

            /**
             * Constructs a new unformatted {@link Builder} with the given content.
             *
             * @param content The content for the text builder
             */
            Builder(String content) {
                content(content);
            }

            /**
             * Constructs a new {@link Builder} with the formatting and actions of
             * the specified {@link Text} and the given content.
             *
             * @param text The text to apply the properties from
             * @param content The content for the text builder
             */
            Builder(Text text, String content) {
                super(text);
                content(content);
            }

            /**
             * Constructs a new {@link Builder} with the formatting, actions and
             * content of the specified {@link Literal}.
             *
             * @param text The text to apply the properties from
             */
            Builder(Literal text) {
                super(text);
                this.content = text.content;
            }

            /**
             * Returns the current content of this builder.
             *
             * @return The current content
             * @see Literal#getContent()
             */
            public final String getContent() {
                return this.content;
            }

            /**
             * Sets the plain text content of this text.
             *
             * @param content The content of this text
             * @return This text builder
             * @see Literal#getContent()
             */
            public Builder content(String content) {
                this.content = checkNotNull(content, "content");
                return this;
            }

            @Override
            public Literal build() {
                // Special case for empty builder
                if (this.content.isEmpty() && this.format.getColor() == TextColors.NONE && this.format.getStyle().isEmpty()
                    && this.children.isEmpty() && this.clickAction == null && this.hoverAction == null
                    && this.shiftClickAction == null) {
                    return Texts.EMPTY;
                }

                return new Literal(
                        this.format,
                        ImmutableList.copyOf(this.children),
                        this.clickAction,
                        this.hoverAction,
                        this.shiftClickAction,
                        this.content);
            }

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof Builder) || !super.equals(o)) {
                    return false;
                }

                Builder that = (Builder) o;
                return Objects.equal(this.content, that.content);

            }

            @Override
            public int hashCode() {
                return Objects.hashCode(super.hashCode(), this.content);
            }

            @Override
            public String toString() {
                return Objects.toStringHelper(this)
                        .addValue(super.toString())
                        .add("content", this.content)
                        .toString();
            }

            @Override
            public Builder color(TextColor color) {
                return (Builder) super.color(color);
            }

            @Override
            public Builder style(TextStyle... styles) {
                return (Builder) super.style(styles);
            }

            @Override
            public Builder onClick(@Nullable ClickAction<?> clickAction) {
                return (Builder) super.onClick(clickAction);
            }

            @Override
            public Builder onHover(@Nullable HoverAction<?> hoverAction) {
                return (Builder) super.onHover(hoverAction);
            }

            @Override
            public Builder onShiftClick(@Nullable ShiftClickAction<?> shiftClickAction) {
                return (Builder) super.onShiftClick(shiftClickAction);
            }

            @Override
            public Builder append(Text... children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder append(Iterable<? extends Text> children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder insert(int pos, Text... children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder insert(int pos, Iterable<? extends Text> children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder remove(Text... children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder remove(Iterable<? extends Text> children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder removeAll() {
                return (Builder) super.removeAll();
            }

        }
    }

    /**
     * Represents a {@link Text} placeholder that can be replaced with another
     * Text by {@link Texts#format(Text, Map)}.
     *
     * @see Builder
     */
    public static class Placeholder extends Text {

        protected final String key;
        private final Optional<Text> fallback;


        Placeholder(String key) {
            this(key, null);
        }

        Placeholder(String key, Text fallback) {
            checkArgument(!checkNotNull(key, "key").isEmpty(), "key cannot be empty");
            this.key = key;
            this.fallback = Optional.ofNullable(fallback);
        }

        /**
         * Constructs a new immutable {@link Placeholder} for the given plain
         * text content with the specified formatting and text actions applied.
         *
         * @param format The format of the text
         * @param children The immutable list of children of the text
         * @param clickAction The click action of the text, or {@code null} for
         *        none
         * @param hoverAction The hover action of the text, or {@code null} for
         *        none
         * @param shiftClickAction The shift click action of the text, or
         *        {@code null} for none
         * @param key The key of the placeholder
         * @param fallback The fallback text if this does not get replaced
         */
        Placeholder(TextFormat format, ImmutableList<Text> children, @Nullable ClickAction<?> clickAction,
                @Nullable HoverAction<?> hoverAction, @Nullable ShiftClickAction<?> shiftClickAction, String key, Text fallback) {
            super(format, children, clickAction, hoverAction, shiftClickAction);
            checkArgument(!checkNotNull(key, "key").isEmpty(), "key cannot be empty");
            this.key = key;
            this.fallback = Optional.ofNullable(fallback);
        }

        /**
         * Returns the placeholder key used to replace this placeholder with the
         * real content.
         *
         * @return The template key of this template
         */
        public final String getKey() {
            return this.key;
        }

        /**
         * Get the fallback text that will be used in place if this placeholder has no value.
         *
         * @return The fallback text
         */
        public Optional<Text> getFallback() {
            return this.fallback;
        }

        @Override
        public Builder builder() {
            return new Builder(this);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Placeholder) || !super.equals(o)) {
                return false;
            }

            Placeholder that = (Placeholder) o;
            return Objects.equal(this.key, that.key) && Objects.equal(this.fallback, that.fallback);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), this.key);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("key", this.key)
                    .add("fallback", this.fallback)
                    .addValue(super.toString())
                    .toString();
        }

        /**
         * Represents a {@link Text.Builder} creating immutable
         * {@link Placeholder} instances.
         *
         * @see Placeholder
         */
        public static class Builder extends Text.Builder {
            private String key;
            private Text fallback;

            /**
             * Constructs a new unformatted {@link Builder} with the given
             * content.
             *
             * @param key The none empty replacement key for the placeholder builder
             */
            Builder(String key) {
                key(key);
            }

            /**
             * Constructs a new {@link Builder} with the formatting and actions
             * of the specified {@link Text} and the given content.
             *
             * @param text The text to apply the properties from
             * @param key The none empty replacement key for the placeholder builder
             */
            Builder(Text text, String key) {
                super(text);
                key(key);
            }

            /**
             * Constructs a new {@link Builder} with the formatting, actions and
             * content of the specified {@link Placeholder}.
             *
             * @param text The text to apply the properties from
             */
            Builder(Placeholder text) {
                super(text);
                this.key = text.key;
                if (text.getFallback().isPresent()) {
                    this.fallback = text.getFallback().get();
                }
            }

            /**
             * Returns the current replacement key of this builder.
             *
             * @return The current replacement key
             * @see Placeholder#getKey()
             */
            public final String getKey() {
                return this.key;
            }

            /**
             * Sets the plain text replacement key of this text.
             *
             * @param key The key of this text
             * @return This text builder
             * @see Placeholder#getKey()
             */
            public Builder key(String key) {
                checkArgument(!checkNotNull(key, "key").isEmpty(), "key cannot be empty");
                this.key = key;
                return this;
            }

            /**
             * Sets the fallback text that will be used if no value is present for this placeholder
             *
             * @param fallback The content of this text
             * @return This text builder
             * @see Placeholder#getFallback()
             */
            public Builder fallback(Text fallback) {
                this.fallback = fallback;
                return this;
            }

            @Override
            public Placeholder build() {
                return new Placeholder(
                        this.format,
                        ImmutableList.copyOf(this.children),
                        this.clickAction,
                        this.hoverAction,
                        this.shiftClickAction,
                        this.key,
                        this.fallback);
            }

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof Builder) || !super.equals(o)) {
                    return false;
                }

                Builder that = (Builder) o;
                return Objects.equal(this.key, that.key) && Objects.equal(this.fallback, that.fallback);

            }

            @Override
            public int hashCode() {
                return Objects.hashCode(super.hashCode(), this.fallback);
            }

            @Override
            public String toString() {
                return Objects.toStringHelper(this)
                        .addValue(super.toString())
                        .add("key", this.key)
                        .add("fallback", this.fallback)
                        .toString();
            }

            @Override
            public Builder color(TextColor color) {
                return (Builder) super.color(color);
            }

            @Override
            public Builder style(TextStyle... styles) {
                return (Builder) super.style(styles);
            }

            @Override
            public Builder onClick(@Nullable ClickAction<?> clickAction) {
                return (Builder) super.onClick(clickAction);
            }

            @Override
            public Builder onHover(@Nullable HoverAction<?> hoverAction) {
                return (Builder) super.onHover(hoverAction);
            }

            @Override
            public Builder onShiftClick(@Nullable ShiftClickAction<?> shiftClickAction) {
                return (Builder) super.onShiftClick(shiftClickAction);
            }

            @Override
            public Builder append(Text... children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder append(Iterable<? extends Text> children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder insert(int pos, Text... children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder insert(int pos, Iterable<? extends Text> children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder remove(Text... children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder remove(Iterable<? extends Text> children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder removeAll() {
                return (Builder) super.removeAll();
            }

        }
    }

    /**
     * Represents a {@link Text} containing a {@link Translation} identifier
     * that gets translated into the current locale on the client.
     *
     * @see TranslatableBuilder
     */
    public static class Translatable extends Text {

        protected final Translation translation;
        protected final ImmutableList<Object> arguments;

        Translatable(Translation translation, ImmutableList<Object> arguments) {
            this.translation = checkNotNull(translation, "translation");
            this.arguments = checkNotNull(arguments, "arguments");
        }

        /**
         * Constructs a new immutable {@link Translatable} for the given
         * translation with the specified formatting and text actions applied.
         *
         * @param format The format of the text
         * @param children The immutable list of children of the text
         * @param clickAction The click action of the text, or {@code null} for
         *        none
         * @param hoverAction The hover action of the text, or {@code null} for
         *        none
         * @param shiftClickAction The shift click action of the text, or
         *        {@code null} for none
         * @param translation The translation of the text
         * @param arguments The arguments for the translation
         */
        Translatable(TextFormat format, ImmutableList<Text> children, @Nullable ClickAction<?> clickAction,
                @Nullable HoverAction<?> hoverAction, @Nullable ShiftClickAction<?> shiftClickAction, Translation translation,
                ImmutableList<Object> arguments) {
            super(format, children, clickAction, hoverAction, shiftClickAction);
            this.translation = checkNotNull(translation, "translation");
            this.arguments = checkNotNull(arguments, "arguments");
        }

        /**
         * Returns the translation of this {@link Text}.
         *
         * @return The translation of this text
         */
        public final Translation getTranslation() {
            return this.translation;
        }

        /**
         * Returns the list of {@link Translation} arguments used to format this
         * {@link Text}.
         *
         * @return The list of translation arguments
         */
        public final ImmutableList<Object> getArguments() {
            return this.arguments;
        }

        @Override
        public TranslatableBuilder builder() {
            return new TranslatableBuilder(this);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Translatable) || !super.equals(o)) {
                return false;
            }

            Translatable that = (Translatable) o;
            return this.translation.equals(that.translation)
                    && this.arguments.equals(that.arguments);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), this.translation, this.arguments);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .addValue(super.toString())
                    .add("translation", this.translation)
                    .add("arguments", this.arguments)
                    .toString();
        }

        /**
         * Represents a {@link Builder} creating immutable
         * {@link Translatable} instances.
         *
         * @see Translatable
         */
        public static class TranslatableBuilder extends Builder {

            protected Translation translation;
            protected ImmutableList<Object> arguments;

            /**
             * Constructs a new unformatted {@link TranslatableBuilder} with the given
             * {@link Translation} and arguments.
             *
             * @param translation The translation for the builder
             * @param args The arguments for the translation
             */
            TranslatableBuilder(Translation translation, Object... args) {
                translation(translation, args);
            }

            /**
             * Constructs a new unformatted {@link TranslatableBuilder} from
             * the given {@link org.spongepowered.api.text.translation.Translatable}
             * .
             *
             * @param translatable The translatable for the builder
             * @param args The arguments for the translation
             */
            TranslatableBuilder(org.spongepowered.api.text.translation.Translatable translatable, Object... args) {
                translation(translatable, args);
            }

            /**
             * Constructs a new {@link TranslatableBuilder} with the formatting and actions
             * of the specified {@link Text} and the given {@link Translation} and
             * arguments.
             *
             * @param text The text to apply the properties from
             * @param translation The translation for the builder
             * @param args The arguments for the translation
             */
            TranslatableBuilder(Text text, Translation translation, Object... args) {
                super(text);
                translation(translation, args);
            }

            /**
             * Constructs a new {@link TranslatableBuilder} with the formatting and actions
             * of the specified {@link Text} and the given
             * {@link org.spongepowered.api.text.translation.Translatable}.
             *
             * @param text The text to apply the properties from
             * @param translatable The translatable for the builder
             * @param args The arguments for the translation
             */
            TranslatableBuilder(Text text, org.spongepowered.api.text.translation.Translatable translatable, Object... args) {
                super(text);
                translation(translatable, args);
            }

            /**
             * Constructs a new {@link TranslatableBuilder} with the formatting, actions
             * and translation of the specified {@link Translatable}.
             *
             * @param text The text to apply the properties from
             */
            TranslatableBuilder(Translatable text) {
                super(text);
                this.translation = text.translation;
                this.arguments = text.arguments;
            }

            /**
             * Returns the current translation of this builder.
             *
             * @return The current content
             * @see Translatable#getTranslation()
             */
            public final Translation getTranslation() {
                return this.translation;
            }

            /**
             * Returns the current translation arguments of this builder.
             *
             * @return The current translation arguments
             * @see Translatable#getArguments()
             */
            public final ImmutableList<Object> getArguments() {
                return this.arguments;
            }

            /**
             * Sets the translation of the text.
             *
             * @param translation The translation to use for this builder
             * @param args The arguments for the translation
             * @return This text builder
             */
            public TranslatableBuilder translation(Translation translation, Object... args) {
                this.translation = checkNotNull(translation, "translation");
                this.arguments = ImmutableList.copyOf(checkNotNull(args, "args"));
                return this;
            }

            /**
             * Sets the translation of the text.
             *
             * @param translatable The translatable object to use for this builder
             * @param args The arguments for the translation
             * @return This text builder
             */
            public TranslatableBuilder translation(org.spongepowered.api.text.translation.Translatable translatable, Object... args) {
                return translation(checkNotNull(translatable, "translatable").getTranslation(), args);
            }

            @Override
            public Translatable build() {
                return new Translatable(
                        this.format,
                        ImmutableList.copyOf(this.children),
                        this.clickAction,
                        this.hoverAction,
                        this.shiftClickAction,
                        this.translation,
                        this.arguments);
            }

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof TranslatableBuilder) || !super.equals(o)) {
                    return false;
                }

                TranslatableBuilder that = (TranslatableBuilder) o;
                return Objects.equal(this.translation, that.translation)
                        && Objects.equal(this.arguments, that.arguments);

            }

            @Override
            public int hashCode() {
                return Objects.hashCode(super.hashCode(), this.translation, this.arguments);
            }

            @Override
            public String toString() {
                return Objects.toStringHelper(this)
                        .addValue(super.toString())
                        .add("translation", this.translation)
                        .add("arguments", this.arguments)
                        .toString();
            }

            @Override
            public TranslatableBuilder color(TextColor color) {
                return (TranslatableBuilder) super.color(color);
            }

            @Override
            public TranslatableBuilder style(TextStyle... styles) {
                return (TranslatableBuilder) super.style(styles);
            }

            @Override
            public TranslatableBuilder onClick(@Nullable ClickAction<?> clickAction) {
                return (TranslatableBuilder) super.onClick(clickAction);
            }

            @Override
            public TranslatableBuilder onHover(@Nullable HoverAction<?> hoverAction) {
                return (TranslatableBuilder) super.onHover(hoverAction);
            }

            @Override
            public TranslatableBuilder onShiftClick(@Nullable ShiftClickAction<?> shiftClickAction) {
                return (TranslatableBuilder) super.onShiftClick(shiftClickAction);
            }

            @Override
            public TranslatableBuilder append(Text... children) {
                return (TranslatableBuilder) super.append(children);
            }

            @Override
            public TranslatableBuilder append(Iterable<? extends Text> children) {
                return (TranslatableBuilder) super.append(children);
            }

            @Override
            public TranslatableBuilder insert(int pos, Text... children) {
                return (TranslatableBuilder) super.insert(pos, children);
            }

            @Override
            public TranslatableBuilder insert(int pos, Iterable<? extends Text> children) {
                return (TranslatableBuilder) super.insert(pos, children);
            }

            @Override
            public TranslatableBuilder remove(Text... children) {
                return (TranslatableBuilder) super.remove(children);
            }

            @Override
            public TranslatableBuilder remove(Iterable<? extends Text> children) {
                return (TranslatableBuilder) super.remove(children);
            }

            @Override
            public TranslatableBuilder removeAll() {
                return (TranslatableBuilder) super.removeAll();
            }

        }
    }

    /**
     * Represents a {@link Text} containing a selector that will be replaced by
     * the names of the matching entities on the client.
     *
     * @see org.spongepowered.api.text.selector.Selector
     * @see Score.Builder
     */
    public static class Selector extends Text {

        protected final org.spongepowered.api.text.selector.Selector selector;

        Selector(org.spongepowered.api.text.selector.Selector selector) {
            this.selector = checkNotNull(selector, "selector");
        }

        /**
         * Constructs a new immutable {@link Selector} for the given selector
         * with the specified formatting and text actions applied.
         *
         * @param format The format of the text
         * @param children The immutable list of children of the text
         * @param clickAction The click action of the text, or {@code null} for
         *        none
         * @param hoverAction The hover action of the text, or {@code null} for
         *        none
         * @param shiftClickAction The shift click action of the text, or
         *        {@code null} for none
         * @param selector The selector of the text
         */
        Selector(TextFormat format, ImmutableList<Text> children, @Nullable ClickAction<?> clickAction,
                @Nullable HoverAction<?> hoverAction, @Nullable ShiftClickAction<?> shiftClickAction,
                org.spongepowered.api.text.selector.Selector selector) {
            super(format, children, clickAction, hoverAction, shiftClickAction);
            this.selector = checkNotNull(selector, "selector");
        }

        /**
         * Returns the selector used in this {@link Text}.
         *
         * @return The selector of this text
         */
        public final org.spongepowered.api.text.selector.Selector getSelector() {
            return this.selector;
        }

        @Override
        public Builder builder() {
            return new Builder(this);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Selector) || !super.equals(o)) {
                return false;
            }

            Selector that = (Selector) o;
            return this.selector.equals(that.selector);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), this.selector);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .addValue(super.toString())
                    .add("selector", this.selector)
                    .toString();
        }

        /**
         * Represents a {@link Text.Builder} creating immutable {@link Selector}
         * instances.
         *
         * @see Selector
         */
        public static class Builder extends Text.Builder {

            protected org.spongepowered.api.text.selector.Selector selector;

            /**
             * Constructs a new unformatted {@link Builder} with the given
             * selector.
             *
             * @param selector The selector for the builder
             */
            Builder(org.spongepowered.api.text.selector.Selector selector) {
                selector(selector);
            }

            /**
             * Constructs a new {@link Builder} with the formatting and actions of
             * the specified {@link Text} and the given selector.
             *
             * @param text The text to apply the properties from
             * @param selector The selector for the builder
             */
            Builder(Text text, org.spongepowered.api.text.selector.Selector selector) {
                super(text);
                selector(selector);
            }

            /**
             * Constructs a new {@link Builder} with the formatting, actions and
             * selector of the specified {@link Selector}.
             *
             * @param text The text to apply the properties from
             */
            Builder(Selector text) {
                super(text);
                this.selector = text.selector;
            }

            /**
             * Returns the current selector of this builder.
             *
             * @return The current selector
             * @see Selector#getSelector()
             */
            public final org.spongepowered.api.text.selector.Selector getSelector() {
                return this.selector;
            }

            /**
             * Sets the selector of the text.
             *
             * @param selector The selector for this builder to use
             * @return This text builder
             * @see Selector#getSelector()
             */
            public Builder selector(org.spongepowered.api.text.selector.Selector selector) {
                this.selector = checkNotNull(selector, "selector");
                return this;
            }

            @Override
            public Selector build() {
                return new Selector(
                        this.format,
                        ImmutableList.copyOf(this.children),
                        this.clickAction,
                        this.hoverAction,
                        this.shiftClickAction,
                        this.selector);
            }

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof Builder) || !super.equals(o)) {
                    return false;
                }

                Builder that = (Builder) o;
                return Objects.equal(this.selector, that.selector);

            }

            @Override
            public int hashCode() {
                return Objects.hashCode(super.hashCode(), this.selector);
            }

            @Override
            public String toString() {
                return Objects.toStringHelper(this)
                        .addValue(super.toString())
                        .add("selector", this.selector)
                        .toString();
            }

            @Override
            public Builder color(TextColor color) {
                return (Builder) super.color(color);
            }

            @Override
            public Builder style(TextStyle... styles) {
                return (Builder) super.style(styles);
            }

            @Override
            public Builder onClick(@Nullable ClickAction<?> clickAction) {
                return (Builder) super.onClick(clickAction);
            }

            @Override
            public Builder onHover(@Nullable HoverAction<?> hoverAction) {
                return (Builder) super.onHover(hoverAction);
            }

            @Override
            public Builder onShiftClick(@Nullable ShiftClickAction<?> shiftClickAction) {
                return (Builder) super.onShiftClick(shiftClickAction);
            }

            @Override
            public Builder append(Text... children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder append(Iterable<? extends Text> children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder insert(int pos, Text... children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder insert(int pos, Iterable<? extends Text> children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder remove(Text... children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder remove(Iterable<? extends Text> children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder removeAll() {
                return (Builder) super.removeAll();
            }

        }
    }

    /**
     * Represents a {@link Text} displaying the current score of a player.
     *
     * @see Builder
     */
    public static class Score extends Text {

        protected final org.spongepowered.api.scoreboard.Score score;
        protected final Optional<String> override;

        Score(org.spongepowered.api.scoreboard.Score score) {
            this.score = checkNotNull(score, "score");
            this.override = Optional.empty();
        }

        /**
         * Constructs a new immutable {@link Score} for the given score with the
         * specified formatting and text actions applied.
         *
         * @param format The format of the text
         * @param children The immutable list of children of the text
         * @param clickAction The click action of the text, or {@code null} for
         *        none
         * @param hoverAction The hover action of the text, or {@code null} for
         *        none
         * @param shiftClickAction The shift click action of the text, or
         *        {@code null} for none
         * @param score The score of the text
         * @param override The text to override the score with, or {@code null}
         *        for none
         */
        Score(TextFormat format, ImmutableList<Text> children, @Nullable ClickAction<?> clickAction,
                @Nullable HoverAction<?> hoverAction, @Nullable ShiftClickAction<?> shiftClickAction,
                org.spongepowered.api.scoreboard.Score score, @Nullable String override) {
            super(format, children, clickAction, hoverAction, shiftClickAction);
            this.score = checkNotNull(score, "score");
            this.override = Optional.ofNullable(override);
        }

        /**
         * Returns the score displayed by this {@link Text}.
         *
         * @return The score in this text
         */
        public final org.spongepowered.api.scoreboard.Score getScore() {
            return this.score;
        }

        /**
         * Returns a value that is displayed instead of the real score.
         *
         * @return The value displayed instead of the real score, or
         *         {@link Optional#empty()} if the real score will be displayed
         *         instead
         */
        public final Optional<String> getOverride() {
            return this.override;
        }

        @Override
        public Builder builder() {
            return new Builder(this);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Score) || !super.equals(o)) {
                return false;
            }

            Score that = (Score) o;
            return this.score.equals(that.score) && this.override.equals(that.override);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), this.score, this.override);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .addValue(super.toString())
                    .add("score", this.score)
                    .add("override", this.override)
                    .toString();
        }

        /**
         * Represents a {@link Text.Builder} creating immutable {@link Score}
         * instances.
         *
         * @see Score
         */
        public static class Builder extends Text.Builder {

            protected org.spongepowered.api.scoreboard.Score score;
            @Nullable protected String override;

            /**
             * Constructs a new unformatted {@link Builder} with the given score.
             *
             * @param score The score for the text builder
             */
            Builder(org.spongepowered.api.scoreboard.Score score) {
                score(score);
            }

            /**
             * Constructs a new {@link Builder} with the formatting and actions of the
             * specified {@link Text} and the given score.
             *
             * @param text The text to apply the properties from
             * @param score The score for the text builder
             */
            Builder(Text text, org.spongepowered.api.scoreboard.Score score) {
                super(text);
                score(score);
            }

            /**
             * Constructs a new {@link Builder} with the formatting, actions and score
             * of the specified {@link Score}.
             *
             * @param text The text to apply the properties from
             */
            Builder(Score text) {
                super(text);
                this.score = text.score;
                this.override = text.override.orElse(null);
            }

            /**
             * Returns the current score of this builder.
             *
             * @return The current score
             * @see Score#getScore()
             */
            public final org.spongepowered.api.scoreboard.Score getScore() {
                return this.score;
            }

            /**
             * Sets the score of the text.
             *
             * @param score The score for this builder to use
             * @return This text builder
             * @see Score#getScore()
             */
            public Builder score(org.spongepowered.api.scoreboard.Score score) {
                this.score = checkNotNull(score, "score");
                return this;
            }

            /**
             * Returns the current override of this builder.
             *
             * @return The current override, or {@link Optional#empty()} if none
             * @see Score#getOverride()
             */
            public final Optional<String> getOverride() {
                return Optional.ofNullable(this.override);
            }

            /**
             * Overrides the real score and displays a custom text instead.
             *
             * @param override The text to override the score with or {@code null}
             *        to reset
             * @return This text builder
             * @see Score#getOverride()
             */
            public Builder override(@Nullable String override) {
                this.override = override;
                return this;
            }

            @Override
            public Score build() {
                return new Score(
                        this.format,
                        ImmutableList.copyOf(this.children),
                        this.clickAction,
                        this.hoverAction,
                        this.shiftClickAction,
                        this.score,
                        this.override);
            }

            @Override
            public boolean equals(@Nullable Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof Builder) || !super.equals(o)) {
                    return false;
                }

                Builder that = (Builder) o;
                return Objects.equal(this.score, that.score)
                        && Objects.equal(this.override, that.override);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(super.hashCode(), this.score, this.override);
            }

            @Override
            public String toString() {
                return Objects.toStringHelper(this)
                        .addValue(super.toString())
                        .add("score", this.score)
                        .add("override", this.override)
                        .toString();
            }

            @Override
            public Builder color(TextColor color) {
                return (Builder) super.color(color);
            }

            @Override
            public Builder style(TextStyle... styles) {
                return (Builder) super.style(styles);
            }

            @Override
            public Builder onClick(@Nullable ClickAction<?> clickAction) {
                return (Builder) super.onClick(clickAction);
            }

            @Override
            public Builder onHover(@Nullable HoverAction<?> hoverAction) {
                return (Builder) super.onHover(hoverAction);
            }

            @Override
            public Builder onShiftClick(@Nullable ShiftClickAction<?> shiftClickAction) {
                return (Builder) super.onShiftClick(shiftClickAction);
            }

            @Override
            public Builder append(Text... children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder append(Iterable<? extends Text> children) {
                return (Builder) super.append(children);
            }

            @Override
            public Builder insert(int pos, Text... children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder insert(int pos, Iterable<? extends Text> children) {
                return (Builder) super.insert(pos, children);
            }

            @Override
            public Builder remove(Text... children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder remove(Iterable<? extends Text> children) {
                return (Builder) super.remove(children);
            }

            @Override
            public Builder removeAll() {
                return (Builder) super.removeAll();
            }

        }
    }

    /**
     * Represents a builder class to create immutable {@link Text} instances.
     *
     * @see Text
     */
    public abstract static class Builder implements TextRepresentable {

        protected TextFormat format = new TextFormat();
        protected List<Text> children = Lists.newArrayList();
        @Nullable protected ClickAction<?> clickAction;
        @Nullable protected HoverAction<?> hoverAction;
        @Nullable protected ShiftClickAction<?> shiftClickAction;

        /**
         * Constructs a new empty {@link Builder}.
         */
        protected Builder() {
        }

        /**
         * Constructs a new {@link Builder} with the properties of the given
         * {@link Text} as initial values.
         *
         * @param text The text to copy the values from
         */
        Builder(Text text) {
            checkNotNull(text, "text");
            this.format = text.format;
            this.children = Lists.newArrayList(text.children);
            this.clickAction = text.clickAction.orElse(null);
            this.hoverAction = text.hoverAction.orElse(null);
            this.shiftClickAction = text.shiftClickAction.orElse(null);
        }

        /**
         * Returns the current format of the {@link Text} in this builder.
         *
         * @return The current format
         * @see Text#getFormat()
         */
        public final TextFormat getFormat() {
            return this.format;
        }

        /**
         * Sets the {@link TextFormat} of this text.
         *
         * @param format The new text format for this text
         * @return The text builder
         * @see Text#getFormat()
         */
        public Builder format(TextFormat format) {
            this.format = checkNotNull(format, "format");
            return this;
        }

        /**
         * Returns the current color of the {@link Text} in this builder.
         *
         * @return The current color
         * @see Text#getColor()
         */
        public final TextColor getColor() {
            return this.format.getColor();
        }

        /**
         * Sets the {@link TextColor} of this text.
         *
         * @param color The new text color for this text
         * @return This text builder
         * @see Text#getColor()
         */
        public Builder color(TextColor color) {
            this.format = this.format.color(checkNotNull(color, "color"));
            return this;
        }

        /**
         * Returns the current style of the {@link Text} in this builder.
         *
         * @return The current style
         * @see Text#getStyle()
         */
        public final TextStyle getStyle() {
            return this.format.getStyle();
        }

        /**
         * Sets the text styles of this text. This will construct a composite
         * {@link TextStyle} of the current style and the specified styles first and
         * set it to the text.
         *
         * @param styles The text styles to apply
         * @return This text builder
         * @see Text#getStyle()
         */
        // TODO: Make sure this is the correct behaviour
        public Builder style(TextStyle... styles) {
            this.format = this.format.style(this.format.getStyle().and(checkNotNull(styles, "styles")));
            return this;
        }

        /**
         * Returns the current {@link ClickAction} of this builder.
         *
         * @return The current click action or {@link Optional#empty()} if none
         * @see Text#getClickAction()
         */
        public final Optional<ClickAction<?>> getClickAction() {
            return Optional.<ClickAction<?>>ofNullable(this.clickAction);
        }

        /**
         * Sets the {@link ClickAction} that will be executed if the text is clicked
         * in the chat.
         *
         * @param clickAction The new click action for the text
         * @return This text builder
         * @see Text#getClickAction()
         */
        public Builder onClick(@Nullable ClickAction<?> clickAction) {
            this.clickAction = clickAction;
            return this;
        }

        /**
         * Returns the current {@link HoverAction} of this builder.
         *
         * @return The current hover action or {@link Optional#empty()} if none
         * @see Text#getHoverAction()
         */
        public final Optional<HoverAction<?>> getHoverAction() {
            return Optional.<HoverAction<?>>ofNullable(this.hoverAction);
        }

        /**
         * Sets the {@link HoverAction} that will be executed if the text is hovered
         * in the chat.
         *
         * @param hoverAction The new hover action for the text
         * @return This text builder
         * @see Text#getHoverAction()
         */
        public Builder onHover(@Nullable HoverAction<?> hoverAction) {
            this.hoverAction = hoverAction;
            return this;
        }

        /**
         * Returns the current {@link ShiftClickAction} of this builder.
         *
         * @return The current shift click action or {@link Optional#empty()} if
         *         none
         * @see Text#getShiftClickAction()
         */
        public final Optional<ShiftClickAction<?>> getShiftClickAction() {
            return Optional.<ShiftClickAction<?>>ofNullable(this.shiftClickAction);
        }

        /**
         * Sets the {@link ShiftClickAction} that will be executed if the text is
         * shift-clicked in the chat.
         *
         * @param shiftClickAction The new shift click action for the text
         * @return This text builder
         * @see Text#getShiftClickAction()
         */
        public Builder onShiftClick(@Nullable ShiftClickAction<?> shiftClickAction) {
            this.shiftClickAction = shiftClickAction;
            return this;
        }

        /**
         * Returns a view of the current children of this builder.
         *
         * <p>The returned list is unmodifiable, but not immutable. It will change
         * if new children get added through this builder.</p>
         *
         * @return An unmodifiable list of the current children
         * @see Text#getChildren()
         */
        public final List<Text> getChildren() {
            return Collections.unmodifiableList(this.children);
        }

        /**
         * Appends the specified {@link Text} to the end of this text.
         *
         * @param children The texts to append
         * @return This text builder
         * @see Text#getChildren()
         */
        public Builder append(Text... children) {
            for (Text child : checkNotNull(children, "children")) {
                checkNotNull(child, "child");
                this.children.add(child);
            }
            return this;
        }

        /**
         * Appends the specified {@link Text} to the end of this text.
         *
         * @param children The texts to append
         * @return This text builder
         * @see Text#getChildren()
         */
        public Builder append(Iterable<? extends Text> children) {
            for (Text child : checkNotNull(children, "children")) {
                this.children.add(checkNotNull(child, "child"));
            }
            return this;
        }

        /**
         * Inserts the specified {@link Text} at the given position of this builder.
         *
         * @param pos The position to insert the texts to
         * @param children The texts to insert
         * @return This text builder
         * @throws IndexOutOfBoundsException If the position is out of bounds
         * @see Text#getChildren()
         */
        public Builder insert(int pos, Text... children) {
            for (Text child : checkNotNull(children, "children")) {
                this.children.add(pos++, checkNotNull(child, "child"));
            }
            return this;
        }

        /**
         * Inserts the specified {@link Text} at the given position of this builder.
         *
         * @param pos The position to insert the texts to
         * @param children The texts to insert
         * @return This text builder
         * @throws IndexOutOfBoundsException If the position is out of range
         * @see Text#getChildren()
         */
        public Builder insert(int pos, Iterable<? extends Text> children) {
            for (Text child : checkNotNull(children, "children")) {
                this.children.add(pos++, checkNotNull(child, "child"));
            }
            return this;
        }

        /**
         * Removes the specified {@link Text} from this builder.
         *
         * @param children The texts to remove
         * @return This text builder
         * @see Text#getChildren()
         */
        public Builder remove(Text... children) {
            for (Text child : checkNotNull(children, "children")) {
                this.children.remove(checkNotNull(child));
            }
            return this;
        }

        /**
         * Removes the specified {@link Text} from this builder.
         *
         * @param children The texts to remove
         * @return This text builder
         * @see Text#getChildren()
         */
        public Builder remove(Iterable<? extends Text> children) {
            for (Text child : checkNotNull(children, "children")) {
                this.children.remove(checkNotNull(child));
            }
            return this;
        }

        /**
         * Removes all children from this builder.
         *
         * @return This text builder
         * @see Text#getChildren()
         */
        public Builder removeAll() {
            this.children.clear();
            return this;
        }

        /**
         * Builds an immutable instance of the current state of this text builder.
         *
         * @return An immutable {@link Text} with the current properties of this
         *         builder
         */
        public abstract Text build();

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Builder)) {
                return false;
            }

            Builder that = (Builder) o;
            return Objects.equal(this.format, that.format)
                    && Objects.equal(this.clickAction, that.clickAction)
                    && Objects.equal(this.hoverAction, that.hoverAction)
                    && Objects.equal(this.shiftClickAction, that.shiftClickAction)
                    && Objects.equal(this.children, that.children);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.format, this.clickAction, this.hoverAction, this.shiftClickAction, this.children);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(Builder.class)
                    .add("format", this.format)
                    .add("children", this.children)
                    .add("clickAction", this.clickAction)
                    .add("hoverAction", this.hoverAction)
                    .add("shiftClickAction", this.shiftClickAction)
                    .toString();
        }

        @Override
        public final Text toText() {
            return build();
        }

    }
}
