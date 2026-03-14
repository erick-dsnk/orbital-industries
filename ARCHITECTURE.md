# Orbital Industries – Architecture Overview

This document describes the foundational packages and systems of the mod. Each system is scaffolding only; intended use for future gameplay is summarized below.

---

## core

- **ConfigManager**: Central configuration for the mod. Loads and saves Forge `Configuration`, exposes typed getters (e.g. greeting, dimension IDs). Intended use: all config (dimension IDs, feature flags, balance) will go through here so other systems stay data-driven.
- **OIModLogger**: Thin wrapper around the mod logger with a consistent prefix. Intended use: subsystems use this for logging so messages are easy to filter and identify.

---

## dimension

- **SpaceDimensionProvider**: Abstract base/descriptor for a space dimension (ID, metadata). Intended use: each space dimension (orbit, moon, etc.) will have a provider registered with DimensionRegistry; subclasses can add sky renderer hooks and world gen config.
- **SpaceWorldProvider**: Base `WorldProvider` for space dimensions. Intended use: all custom space dimensions use this (or a subclass); behavior is driven by registration and config, not hardcoded IDs.
- **DimensionRegistry**: Single place to register dimension IDs and provider types with the game. Intended use: all dimension registration goes through here; later it can be driven by Planet data or config so new planets/dimensions don’t require code changes.

---

## planet

- **Planet**: Data object for a celestial body (id, name, dimensionId, gravity, atmosphere, orbit radius). Intended use: single source of truth for celestial data; no game logic inside, only fields used by GravityManager, AtmosphereManager, and dimension/transport code.
- **PlanetRegistry**: In-memory registry of planets by ID and by dimension ID. Intended use: register planets from config or data files; lookup by ID or dimension for all other systems.
- **PlanetManager**: Facade over PlanetRegistry and dimension/orbit logic. Intended use: “current planet for this world”, “list all planets”, and similar high-level queries for UI, travel, and environment.

---

## space

- **OrbitalEnvironmentManager**: Determines whether a dimension or world is “space” (e.g. orbit) vs planet surface. Intended use: gameplay and rendering (e.g. vacuum, sky, radiation) will branch on this; later extended with radiation/temperature per zone.
- **GravityManager**: Interface for gravity multiplier per dimension or planet. Intended use: fall damage and entity movement will use this so each dimension/planet can have different gravity; implementation reads from Planet data.
- **AtmosphereManager**: Interface for whether a dimension/planet has breathable atmosphere. Intended use: OxygenSystem and VacuumDamageHandler will use this to decide when the player is in vacuum and needs sealed areas or oxygen.

---

## transport

- **LaunchManager**: Handles launch from surface to orbit. Intended use: fuel checks, countdown, and transition to space dimension; will call TeleportManager and use RocketInterface for vehicle state.
- **TeleportManager**: Handles teleportation between dimensions (e.g. orbit to planet surface). Intended use: target validation, safe spawn position (using PositionUtils), and actual dimension change; used by LaunchManager and later by landing/entry logic.
- **RocketInterface**: Interface for rocket-like vehicles (e.g. canLaunch, destination, fuel). Intended use: any vehicle that can launch (entity or block-based) will implement this so LaunchManager and UI can work with a single contract.

---

## environment

- **VacuumDamageHandler**: Applies damage when the player is in vacuum without oxygen. Intended use: hook into player tick or LivingUpdate; use AtmosphereManager and OxygenSystem to decide when to apply damage.
- **OxygenSystem**: Interface for oxygen presence and level at a position. Intended use: sealed structures and oxygen-providing blocks will be detected so hasOxygen is true inside them; VacuumDamageHandler will only damage when not in a sealed area and dimension has no atmosphere.

---

## registry

- **BlockRegistry**, **ItemRegistry**, **EntityRegistry**: Central registration for blocks, items, and entities with the game. Intended use: all mod blocks, items, and entities are created and registered here during preInit so content stays in one place and naming is consistent.

---

## network

- **PacketHandler**: Registers the mod’s network channel and packet types. Intended use: single place to register all packets (e.g. dimension sync, rocket state, GUI updates); concrete packet classes extend BasePacket and are registered with the channel.
- **BasePacket**: Abstract packet with read/write payload. Intended use: all custom packets extend this and implement readPayload/writePayload; ensures a consistent serialization contract and simplifies adding new packet types.

---

## util

- **OrbitMath**: Static helpers for gravity, orbital period, and escape velocity. Intended use: GravityManager and transport systems will use these for scaling and travel logic; pure functions with no game state.
- **PositionUtils**: Static helpers for chunk/block coordinates and safe spawn position in a dimension. Intended use: TeleportManager and dimension code will use these for target position and finding a safe Y when sending players to a new dimension.
