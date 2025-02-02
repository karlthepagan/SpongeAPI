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
package org.spongepowered.api.world;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.context.Contextual;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.storage.WorldStorage;
import org.spongepowered.api.world.weather.WeatherUniverse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A loaded Minecraft world.
 */
public interface World extends Extent, WeatherUniverse, Viewer, Contextual {

    @Override
    default Location<World> getLocation(Vector3i position) {
        return new Location<>(this, position);
    }

    @Override
    default Location<World> getLocation(int x, int y, int z) {
        return getLocation(new Vector3i(x, y, z));
    }

    @Override
    default Location<World> getLocation(Vector3d position) {
        return new Location<>(this, position);
    }

    @Override
    default Location<World> getLocation(double x, double y, double z) {
        return getLocation(new Vector3d(x, y, z));
    }

    /**
     * Gets the {@link Difficulty} setting for this world.
     *
     * @return Difficulty of the world
     */
    Difficulty getDifficulty();

    /**
     * Gets the name of the world.
     *
     * <p>The world name may randomly generated or user-defined. It may or may
     * not be safe to be used in a filename.</p>
     *
     * @return The world name
     * @see #getUniqueId() A method to get a unique identifier
     */
    String getName();

    /**
     * Get the loaded chunk at the given position.
     *
     * @param position The position
     * @return The chunk, if available
     */
    Optional<Chunk> getChunk(Vector3i position);

    /**
     * Get the loaded chunk at the given position.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return The chunk, if available
     */
    Optional<Chunk> getChunk(int x, int y, int z);

    /**
     * Get the chunk at the given position if it exists or if
     * {@code shouldGenerate} is true and the chunk is generated.
     *
     * @param position The position
     * @param shouldGenerate True to generate a new chunk
     * @return The loaded or generated chunk, if already generated
     */
    Optional<Chunk> loadChunk(Vector3i position, boolean shouldGenerate);

    /**
     * Get the chunk at the given position if it exists or if
     * {@code shouldGenerate} is true and the chunk is generated.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @param shouldGenerate True to generate a new chunk
     * @return The loaded or generated chunk, if already generated
     */
    Optional<Chunk> loadChunk(int x, int y, int z, boolean shouldGenerate);

    /**
     * Unloads the given chunk from the world. Returns a {@code boolean} flag
     * for whether the operation was successful.
     *
     * @param chunk The chunk to unload
     * @return Whether the operation was successful
     */
    boolean unloadChunk(Chunk chunk);

    /**
     * Returns a Collection of all actively loaded chunks in this world.
     *
     * <p>The ordering of the returned chunks is undefined.</p>
     *
     * @return The loaded chunks
     */
    Iterable<Chunk> getLoadedChunks();

    /**
     * Gets the entity whose {@link UUID} matches the provided id, possibly
     * returning no entity if the entity is not loaded or non-existant.
     *
     * <p>For world implementations, only some parts of the world is usually
     * loaded, so this method may return no entity if the entity is not
     * loaded.</p>
     *
     * @param uuid The unique id
     * @return An entity, if available
     */
    Optional<Entity> getEntity(UUID uuid);

    /**
     * Gets the world border for the world.
     *
     * @return The world border
     */
    WorldBorder getWorldBorder();

    /**
     * Returns a new builder for creating a task to pre-generate the chunks
     * inside a square border with a given center and diameter.
     *
     * @param center The center of the border
     * @param diameter The diameter of the border
     * @return The builder for the chunk pre-generate task
     * @see WorldBorder.ChunkPreGenerate
     */
    WorldBorder.ChunkPreGenerate newChunkPreGenerate(Vector3d center, double diameter);

    /**
     * Gets the specified GameRule value.
     **
     * @param gameRule The name of the GameRule.
     * @return The GameRule value, if it exists.
     */
    Optional<String> getGameRule(String gameRule);

    /**
     * Gets a map of the currently set game rules and their values.
     *
     * @return An immutable map of the game rules
     */
    Map<String, String> getGameRules();

    /**
     * Returns the {@link Dimension} of this world.
     *
     * @return The {@link Dimension}
     */
    Dimension getDimension();

    /**
     * Gets the {@link WorldGenerator} for this world.
     *
     * <p>Any changes made to the world generator will only affect newly
     * generated chunks.</p>
     *
     * @return The world generator
     */
    WorldGenerator getWorldGenerator();

    /**
     * Returns whether this {@link World}'s spawn chunks remain loaded when no
     * players are present. Note: This method will default to this {@link World}
     * 's {@link DimensionType}'s keepLoaded value unless a plugin overrides it.
     *
     * @return True if {@link World} remains loaded without players, false if
     *         not
     */
    boolean doesKeepSpawnLoaded();

    /**
     * Sets whether this {@link World}'s spawn chunks remain loaded when no
     * players are present. Note: This method will override the default
     * {@link DimensionType}'s keepLoaded value.
     *
     * @param keepLoaded Whether this {@link World}'s spawn chunks remain loaded
     *        without players
     */
    void setKeepSpawnLoaded(boolean keepLoaded);

    /**
     * Gets the associated {@link WorldStorage} persisting this world.
     *
     * @return The associated world storage
     */
    WorldStorage getWorldStorage();

    /**
     * Gets the @link Scoreboard} for this world.
     *
     * @return The associated {@link Scoreboard}
     */
    Scoreboard getScoreboard();

    /**
     * Sets the {@link Scoreboard} for this world.
     *
     * @param scoreboard The scoreboard to set
     */
    void setScoreboard(Scoreboard scoreboard);

    /**
     * Gets the {@link WorldCreationSettings} which were used to create this
     * world.
     *
     * @return The settings
     */
    WorldCreationSettings getCreationSettings();

    /**
     * Gets the properties for this world.
     *
     * @return The properties
     */
    WorldProperties getProperties();

    /**
     * Gets the {@link Location} of the spawn point.
     * 
     * @return The location
     */
    Location<World> getSpawnLocation();

    /**
     * Causes an {@link Explosion} in a world.
     *
     * @param explosion The explosion to cause
     */
    void triggerExplosion(Explosion explosion);

    /**
     * Gets the teleporter agent, used for manipulating teleporters.
     *
     * @return The teleporter agent
     */
    TeleporterAgent getTeleporterAgent();

    /**
     * Gets the world's player simulator, used for simulating player actions.
     *
     * @return The player simulator
     */
    PlayerSimulator getPlayerSimulator();

}
