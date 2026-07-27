<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import * as THREE from 'three';
  import { tweened } from 'svelte/motion';
  import { cubicOut } from 'svelte/easing';

  interface Props {
    spinTrigger?: number;
    spinDirection?: 1 | -1;
  }

  let { spinTrigger = 0, spinDirection = 1 }: Props = $props();

  const particleCount = 2000;
  const positions = new Float32Array(particleCount * 3);
  const colors = new Float32Array(particleCount * 3);

  const colorPrimary = new THREE.Color('#19E6D2');
  const colorSecondary = new THREE.Color('#159FE8');
  const colorTertiary = new THREE.Color('#8B5CF6');

  for (let i = 0; i < particleCount; i++) {
    const radius = 15 + Math.random() * 20;
    const theta = Math.random() * Math.PI * 2;
    const z = (Math.random() - 0.5) * 80;

    positions[i * 3 + 0] = radius * Math.cos(theta);
    positions[i * 3 + 1] = radius * Math.sin(theta);
    positions[i * 3 + 2] = z;

    const rand = Math.random();
    const color = rand > 0.6 ? colorPrimary : rand > 0.3 ? colorSecondary : colorTertiary;
    colors[i * 3 + 0] = color.r;
    colors[i * 3 + 1] = color.g;
    colors[i * 3 + 2] = color.b;
  }

  const geometry = new THREE.BufferGeometry();
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3));

  let rotationZ = $state(0);
  let scrollVelocity = $state(0);
  let lastScrollY = 0;

  // Discrete spin state for navigation
  const spinOffset = tweened(0, {
    duration: 1200,
    easing: cubicOut
  });

  // Effect to handle navigation spin
  $effect(() => {
    if (spinTrigger > 0) {
      // Add exactly 360 degrees (2*PI) to the current offset
      spinOffset.update(n => n + (Math.PI * 2 * spinDirection));
    }
  });

  // Effect for scroll handling
  $effect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY;
      scrollVelocity = (currentScrollY - lastScrollY) * 0.4;
      lastScrollY = currentScrollY;
    };
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  });

  useTask((delta) => {
    // 1. Constant slow base rotation
    rotationZ += delta * 0.08;

    // 2. Add scroll speed influence
    rotationZ += scrollVelocity * 0.004;

    // 3. Amortization
    scrollVelocity *= 0.94;
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 0, 35]} fov={50} />

<T.PointLight position={[0, 0, 15]} intensity={3} color="#19E6D2" />
<T.AmbientLight intensity={0.2} />

<!--
   The total rotation is the combination of:
   1. rotationZ: Incremental value for auto-rotation and scroll.
   2. $spinOffset: Tweened value that adds a fixed turn on navigation and STOPS.
-->
<T.Group rotation.z={rotationZ + $spinOffset}>
    <T.Points {geometry}>
      <T.PointsMaterial
        size={0.3}
        vertexColors
        transparent
        opacity={0.6}
        sizeAttenuation={true}
        blending={THREE.AdditiveBlending}
        depthWrite={false}
      />
    </T.Points>
</T.Group>
