
package net.mcreator.biomes.block;

import net.minecraft.block.material.Material;

@BiomesModElements.ModElement.Tag
public class RockyPlantBlock extends BiomesModElements.ModElement {

	@ObjectHolder("biomes:rocky_plant")
	public static final Block block = null;

	public RockyPlantBlock(BiomesModElements instance) {
		super(instance, 4);

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustomFlower());
		elements.items.add(() -> new BlockItem(block, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(block.getRegistryName()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientLoad(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(block, RenderType.getCutout());
	}

	@SubscribeEvent
	public void addFeatureToBiomes(BiomeLoadingEvent event) {
		boolean biomeCriteria = false;
		if (new ResourceLocation("biomes:rocky_plains").equals(event.getName()))
			biomeCriteria = true;
		if (!biomeCriteria)
			return;

		FlowersFeature feature = new DefaultFlowersFeature(BlockClusterFeatureConfig.field_236587_a_) {
			@Override
			public BlockState getFlowerToPlace(Random random, BlockPos bp, BlockClusterFeatureConfig fc) {
				return block.getDefaultState();
			}

			@Override
			public boolean generate(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, BlockClusterFeatureConfig config) {
				RegistryKey<World> dimensionType = world.getWorld().getDimensionKey();
				boolean dimensionCriteria = false;

				if (dimensionType == World.OVERWORLD)
					dimensionCriteria = true;

				if (!dimensionCriteria)
					return false;

				return super.generate(world, generator, random, pos, config);
			}
		};

		event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> (ConfiguredFeature<?, ?>) feature
				.withConfiguration(
						(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(block.getDefaultState()), new SimpleBlockPlacer()))
								.tries(64)

								.build())
				.withPlacement(Features.Placements.VEGETATION_PLACEMENT).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).func_242731_b(1));
	}

	public static class BlockCustomFlower extends FlowerBlock {

		public BlockCustomFlower() {
			super(Effects.SATURATION, 0, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().sound(SoundType.PLANT)
					.hardnessAndResistance(0f, 0f).setLightLevel(s -> 0));
			setRegistryName("rocky_plant");
		}

		@Override
		public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
			return 100;
		}

		@Override
		public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
			return 60;
		}

		@Override
		public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if (!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(new ItemStack(this, 1));
		}

		@Override
		public PlantType getPlantType(IBlockReader world, BlockPos pos) {
			return PlantType.PLAINS;
		}

	}

}
