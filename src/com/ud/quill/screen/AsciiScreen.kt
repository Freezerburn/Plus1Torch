package com.ud.quill.screen

import sun.plugin.dom.exception.InvalidStateException
import java.util.*

/**
 * Created by vince_000 on 11/23/2015.
 */


enum class ScreenLayer(val layer: Int) {
    BACKGROUND(0),
    PLAY(1),
    UI(2)
}


fun <T> ArrayList<T>.pop(): T {
    return this.removeAt(this.size - 1);
}

fun get1d(x: Int, y: Int, stride: Int): Int {
    return x + y * stride;
}


class AsciiScreen(internal var width: Int, internal var height: Int) {
    internal val maxItemsPerLayer = width * height;
    internal val pooledItems = ArrayList<ItemData>(maxItemsPerLayer * 3 + 1);
    internal val pooledItemProxies = ArrayList<PlacedItem>(maxItemsPerLayer * 3 + 1);
    internal val actionQueue = ArrayList<QueuedAction>(50);
    /**
     * Three layers: Background, Main ("play" area), UI
     *
     * Background generally has stuff that you don't interact with and is just there to add some flavor to a scene. It
     * is always below the other two layers.
     *
     * Main generally has the player, enemies, etc. Basically, stuff you interact with.
     *
     * UI is generally the bits of the screen that represent stuff like the inventory. You know, the UI :) This layer
     * will always be on top of the other layers.
     */
    internal val layers = ArrayList<ArrayList<ItemData?>>(ScreenLayer.UI.layer);

    init {
        for(i in 0..(maxItemsPerLayer * 3)) {
            pooledItems.add(ItemData(' ', -1, -1, -1, -1));
            pooledItemProxies.add(PlacedItem(this));
        }
        for(i in 0..ScreenLayer.UI.layer) {
            layers.add(ArrayList<ItemData?>(maxItemsPerLayer));
            for(j in 0..maxItemsPerLayer) {
                layers[j].add(null);
            }
        }
    }

    fun place(c: Char, x: Int, y: Int, w: Int = 1, h: Int = 1, layer: ScreenLayer = ScreenLayer.PLAY): PlacedItem {
        val item = pooledItems.pop();
        var wrapper = pooledItemProxies.pop();
        wrapper.item = item;
        wrapper.layer = layer.layer;
        actionQueue.add(QueuedAction.PlacementAction(
                item(c, x, y, w, h), layer.layer
        ));
        return wrapper;
    }

    fun clear() {
        if(actionQueue.isNotEmpty()) {
            throw InvalidStateException("Cannot clear the screen when actions are queued!");
        }
        layers.forEach {
            it.forEach { it ?: pooledItems.add(it!!) }
            it.clear()
        }
    }

    fun render() {
        resolveActions();
    }

    private fun resolveActions() {
    }
}

class PlacedItem(private val screen: AsciiScreen) {
    lateinit internal var item: ItemData;
    internal var layer: Int = -1;

    var x: Int
        get() = item.x;
        set(v) = move(dx = v - x);
    var y: Int
        get() = item.y;
        set(v) = move(dy = v - y);
    var w: Int
        get() = item.w;
        set(v) = resize(dw = v - w);
    var h: Int
        get() = item.h
        set(v) = resize(dh = v - h);

    fun move(dx: Int = 0, dy: Int = 0) {
        screen.actionQueue.add(QueuedAction.MovementAction(item, dx, dy));
    }

    fun moveAbsolute(x: Int = 0, y: Int = 0) {
        screen.actionQueue.add(QueuedAction.MovementAction(item, x, y, absolute = true));
    }

    fun resize(dw: Int = 0, dh: Int = 0) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, dw, dh));
    }

    fun resizeAbsolute(w: Int = 0, h: Int = 0) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, w, h, absolute = true));
    }

    fun remove() {
        screen.actionQueue.add(QueuedAction.RemoveAction(item, layer));
    }
}

internal sealed class QueuedAction {
    private var hasRun = false;

    final fun invoke(screen: AsciiScreen) {
        if(hasRun) {
            throw IllegalStateException("Cannot run an action that has already been run.");
        }
        run(screen);
        hasRun = true;
    }
    final fun undo(screen: AsciiScreen) {
        if(!hasRun) {
            throw IllegalStateException("Cannot undo an action that has not been run.");
        }
        undoInternal(screen);
    }

    abstract internal fun run(screen: AsciiScreen);
    abstract fun undoInternal(screen: AsciiScreen);

    class PlacementAction(private val item: ItemData,
                          private val layer: Int) : QueuedAction() {
        override fun run(screen: AsciiScreen) {
            val loc = get1d(item.x, item.y, screen.width);
            val existing = screen.layers[layer][loc];
            throw UnsupportedOperationException()
        }

        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }
    }

    class RemoveAction(private val item: ItemData,
                       private val layer: Int) : QueuedAction() {
        override fun run(screen: AsciiScreen) {
            if(!screen.layers[layer].remove(item)) {
                throw IllegalStateException("Cannot remove '$item' which does not exist.");
            }
        }

        override fun undoInternal(screen: AsciiScreen) {
            screen.layers[layer].add(item);
        }
    }

    class DrawableChangeAction(private val item: ItemData,
                               private val newDraw: Char, private val oldDraw: Char) : QueuedAction() {
        override fun run(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }
    }

    class MovementAction(private val item: ItemData,
                         private val dx: Int, private val dy: Int,
                         private val absolute: Boolean = false) : QueuedAction() {
        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun run(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }
    }

    class ResizeAction(private val item: ItemData,
                       private val dw: Int, private val dh: Int,
                       private val absolute: Boolean = false): QueuedAction() {
        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun run(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }
    }
}

internal class ItemData(var toDraw: Char,
                        var x: Int, var y: Int,
                        var w: Int, var h: Int) {
    constructor() : this(' ', -1, -1, -1, -1) {}

    operator fun invoke(newDraw: Char, newX: Int, newY: Int, newW: Int, newH: Int): ItemData {
        toDraw = newDraw;
        x = newX;
        y = newY;
        w = newW;
        h = newH;
        return this;
    }
}

internal class DataStore(private var width: Int, private var height: Int) {
    fun addItem(item: ItemData): DataStoreUpdater {
        return DataStoreUpdater(item);
    }

    fun updateItem(item: ItemData) {
    }

    fun itemExistsAt(x: Int, y: Int): Boolean {
        return false;
    }
}

internal class DataStoreUpdater(internal val item: ItemData) {
    fun update() {
    }
}