### Added

- Ported to 26.3.
- Added `minKeptDurability` config option to set the minimum durability percentage kept items can be reduced to by death
  durability loss.
- Added `allowDurabilityBreak` config option to toggle whether death durability loss can break kept items (default
  `true`).

### Fixed

- Fixed the accessories screen desyncing after death when a kept accessory adds extra slots, which could also kick players from servers.
- Fixed Curios slots being handled twice on death when the Accessories Compatibility Layer is installed.
