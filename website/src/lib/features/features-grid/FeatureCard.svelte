<script lang="ts">
  import GlassPanel from '../../components/GlassPanel.svelte';

  interface Props {
    icon: string;
    title: string;
    description: string;
    badgeColor?: string;
    iconColor?: string;
  }

  let {
    icon,
    title,
    description,
    badgeColor = 'bg-[#19E6D2]/10 border-[#19E6D2]/20',
    iconColor = 'text-[#19E6D2]'
  }: Props = $props();
</script>

<GlassPanel class="p-8 rounded-xl flex flex-col gap-4 group" glow>
  <div class="w-12 h-12 rounded-lg flex items-center justify-center border {badgeColor} group-hover:scale-110 transition-transform">
    <span class="material-symbols-outlined text-3xl {iconColor}">{icon}</span>
  </div>
  <h3 class="text-xl font-bold text-[#e1e2ec]">{title}</h3>
  <p class="text-sm text-[#bacac6] leading-relaxed">{description}</p>
</GlassPanel>
