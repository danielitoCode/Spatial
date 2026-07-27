<script lang="ts">
  import { Canvas, T, useTask } from '@threlte/core';
  import { Grid as ThrelteGrid, ContactShadows, Float } from '@threlte/extras';
  import * as THREE from 'three';

  interface Props {
    shape?: 'box' | 'sphere' | 'torus' | 'cylinder';
    color?: string;
    emissive?: string;
    metalness?: number;
    roughness?: number;
    wireframe?: boolean;
    lightIntensity?: number;
    lightColor?: string;
    autoRotate?: boolean;
    rotationSpeed?: number;
  }

  let {
    shape = 'box',
    color = '#19E6D2',
    emissive = '#000000',
    metalness = 0.5,
    roughness = 0.2,
    wireframe = false,
    lightIntensity = 10,
    lightColor = '#19E6D2',
    autoRotate = true,
    rotationSpeed = 1
  }: Props = $props();

  let meshRef = $state<THREE.Mesh | undefined>(undefined);

  /*useTask((delta) => {
    if (!meshRef || !autoRotate) return;
    meshRef.rotation.y += delta * 0.5 * rotationSpeed;
    meshRef.rotation.z += delta * 0.2 * rotationSpeed;
  });*/
</script>

<div class="relative w-full h-full min-h-[400px] bg-[#05070D] rounded-[2rem] overflow-hidden border border-[#1C2638] shadow-2xl">
  <Canvas>
    <T.PerspectiveCamera makeDefault position={[0, 2, 5]} fov={45}>
      <!-- Look at center -->
    </T.PerspectiveCamera>

    <T.AmbientLight intensity={0.2} />
    <T.PointLight position={[5, 5, 5]} intensity={lightIntensity} color={lightColor} />
    <T.PointLight position={[-5, 5, 2]} intensity={lightIntensity * 0.5} color="#8B5CF6" />
    <T.SpotLight position={[0, 10, 0]} intensity={2} penumbra={1} castShadow />

    <ThrelteGrid
      sectionSize={1}
      sectionThickness={1}
      cellSize={0.5}
      cellThickness={0.5}
      infiniteGrid
      fadeDistance={15}
      cellColor="#1C2638"
      sectionColor="#19E6D2"
      opacity={0.2}
    />

    <Float speed={2} rotationIntensity={0.5} floatIntensity={0.5}>
      <T.Mesh bind:ref={meshRef} position={[0, 1, 0]} castShadow>
        {#if shape === 'box'}
          <T.BoxGeometry args={[1.5, 1.5, 1.5]} />
        {:else if shape === 'sphere'}
          <T.SphereGeometry args={[1.1, 64, 64]} />
        {:else if shape === 'torus'}
          <T.TorusGeometry args={[1, 0.4, 32, 100]} />
        {:else if shape === 'cylinder'}
          <T.CylinderGeometry args={[0.8, 0.8, 1.8, 64]} />
        {/if}

        <T.MeshStandardMaterial
          {color}
          {emissive}
          {metalness}
          {roughness}
          {wireframe}
        />
      </T.Mesh>
    </Float>

    <ContactShadows
      opacity={0.4}
      scale={10}
      blur={2}
      far={4.5}
    />
  </Canvas>

  <!-- Overlay HUD -->
  <div class="absolute top-6 left-6 flex flex-col gap-1">
    <div class="bg-[#19E6D2]/10 backdrop-blur-md px-3 py-1 rounded-full border border-[#19E6D2]/30 text-[10px] font-black text-[#19E6D2] tracking-widest uppercase">
      Spatial Studio v1.0
    </div>
    <div class="text-[9px] font-mono text-[#6F7A90] ml-1">
      RENDER_ENGINE: OPENGL_ES_3.0_EMULATED
    </div>
  </div>

  <div class="absolute bottom-6 left-6 right-6 flex justify-between items-end pointer-events-none">
    <div class="bg-[#0A0E17]/60 backdrop-blur-sm p-3 rounded-2xl border border-[#1C2638] pointer-events-auto">
       <div class="text-[9px] font-mono text-[#6F7A90] mb-1 uppercase tracking-tighter">Viewport Settings</div>
       <div class="flex gap-2">
          <div class="w-2 h-2 rounded-full bg-[#19E6D2] animate-pulse"></div>
          <div class="w-2 h-2 rounded-full bg-[#8B5CF6]"></div>
          <div class="w-2 h-2 rounded-full bg-[#F25933]"></div>
       </div>
    </div>
  </div>
</div>
