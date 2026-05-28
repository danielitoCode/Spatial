# Camera state synchronization contract

## Atomic operations
All camera writes must be expressed as atomic operations (`orbitTo`, `applyDelta`, `zoomTo`).
No writer should mutate yaw/pitch/zoom separately.

## Thread policy
`SpatialComposeRuntimeBridge` acts as event ingress. Producers can submit input from any thread, but
runtime must serialize camera writes on Main/UI or a single runtime event loop.

## Snapshot publication
After each atomic write, runtime publishes one immutable `CameraSnapshot` at a synchronization point.
Consumers must read only from this published snapshot.

## Source precedence
When multiple sources contend in the same synchronization window, precedence is:
1. Gesture
2. Remote
3. Animation

Lower-precedence writes are ignored in favor of higher-precedence writes.