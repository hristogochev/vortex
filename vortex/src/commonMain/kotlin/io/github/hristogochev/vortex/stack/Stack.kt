package io.github.hristogochev.vortex.stack

public inline fun <reified I : Item, Item> Stack<Item>.popUntil(): Boolean =
    popUntil { item -> item is I }

public enum class StackEvent {
    Push,
    Replace,
    Pop,
    PopGesture,
    Idle
}

public fun StackEvent.isDisposableEvent(): Boolean =
    this == StackEvent.Pop || this == StackEvent.Replace || this == StackEvent.PopGesture

public interface Stack<Item> {

    public val items: List<Item>

    public val lastEvent: StackEvent

    public val lastItemOrNull: Item?

    public val lastItem: Item

    public var current: Item

    public val size: Int

    public val isEmpty: Boolean

    public val canPop: Boolean

    public infix fun push(item: Item)

    public infix fun push(items: List<Item>)

    public infix fun replace(item: Item)

    public infix fun replaceAll(item: Item)

    public infix fun replaceAll(items: List<Item>)

    public infix fun replaceAllWith(predicate: (List<Item>) -> List<Item>)

    public fun replaceUntil(item: Item, predicate: (Item) -> Boolean)

    public fun replaceUntil(items: List<Item>, predicate: (Item) -> Boolean)

    public fun pop(): Boolean

    public fun popAll()

    public infix fun popUntil(predicate: (Item) -> Boolean): Boolean

    public fun popGesture(): Boolean

    public fun popGestureAll()

    public infix fun popGestureUntil(predicate: (Item) -> Boolean): Boolean

    public operator fun plusAssign(item: Item)

    public operator fun plusAssign(items: List<Item>)

    public fun clearEvent()
}
