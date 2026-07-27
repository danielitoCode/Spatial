<script lang="ts">
    import {Item, Row} from "svelte-layouts";
    import SpatialButton from '../../components/SpatialButton.svelte';

    interface Props {
        activeRoute?: string;
        onNavigate?: (route: string) => void;
    }

    let { activeRoute = 'home', onNavigate }: Props = $props();

    function navigate(e: MouseEvent, targetRoute: string) {
        // If it's a standard link, we don't necessarily need to prevent default
        // as long as the hashchange listener is active.
        // However, for the 'Open Studio' button which has no href, we need manual nav.
        if (onNavigate) {
            e.preventDefault();
            onNavigate(targetRoute);
        }
    }
</script>

<nav class="w-full top-0 sticky z-50 border-b border-[#1C2638] bg-[#05070D]/90 backdrop-blur-md" >
    <Row
            justifyContent="space-between"
            alignContent="center"
            alignItems="center"
            padding="15px"
            gap="8px"
    >
        <Item>
            <!-- Brand Logo -->
            <a href="#home" onclick={(e) => navigate(e, 'home')} class="flex items-center gap-3 group">
                <img
                        src="/spatial_icon_cleaned.svg"
                        alt="Spatial Logo"
                        class="w-15 h-15 object-contain group-hover:scale-105 transition-transform"
                />
                <span class="font-extrabold text-2xl tracking-tighter text-[#7effed]">SPATIAL</span>
            </a>
        </Item>
        <Item>
            <Row
                    justifyContent="end"
                    gap="15px" >
                <Item>
                    <a
                            href="#home"
                            onclick={(e) => navigate(e, 'home')}
                            class="transition-colors pb-0.5 {activeRoute === 'home'
                                 ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
                                    : 'text-[#bacac6] hover:text-[#7effed]'}"
                    >
                        Home
                    </a>
                </Item>
                <Item>
                    <a
                            href="#docs"
                            onclick={(e) => navigate(e, 'docs')}
                            class="transition-colors pb-0.5 {activeRoute === 'docs'
                                ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
                                : 'text-[#bacac6] hover:text-[#7effed]'}"
                    >
                        Documentation
                    </a>
                </Item>
                <Item>
                    <a
                            href="#examples"
                            onclick={(e) => navigate(e, 'examples')}
                            class="transition-colors pb-0.5 {activeRoute === 'examples'
                                 ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
                                : 'text-[#bacac6] hover:text-[#7effed]'}"
                    >
                        Examples
                    </a>
                </Item>
                <Item>
                    <a
                            href="#playground"
                            onclick={(e) => navigate(e, 'playground')}
                            class="transition-colors pb-0.5 flex items-center gap-1 {activeRoute === 'playground'
                                ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
                                : 'text-[#bacac6] hover:text-[#7effed]'}"
                    >
                        <span class="material-symbols-outlined text-sm text-[#8B5CF6]">token</span>
                        Playground
                    </a>

                </Item>
                <Item>
                    <a
                            href="#roadmap"
                            onclick={(e) => navigate(e, 'roadmap')}
                            class="transition-colors pb-0.5 flex items-center gap-1 {activeRoute === 'roadmap'
                                ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
                                : 'text-[#bacac6] hover:text-[#7effed]'}"
                    >
                        Roadmap
                    </a>
                </Item>
                <Item>
                    <a
                            href="https://github.com/danielitoCode/Spatial"
                            target="_blank"
                            rel="noreferrer"
                            class="text-[#bacac6] hover:text-[#7effed] transition-colors flex items-center gap-1"
                    >
                        GitHub
                        <span class="material-symbols-outlined text-xs">open_in_new</span>
                    </a>
                </Item>
                <Item>
                    <!-- Get Started CTA -->
                    <SpatialButton variant="primary" size="sm" on:click={ (e) => navigate(e, 'playground')}>
                        Open Studio
                    </SpatialButton>
                </Item>
            </Row>
        </Item>
    </Row>
</nav>