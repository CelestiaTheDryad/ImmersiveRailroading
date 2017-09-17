package cam72cam.immersiverailroading.track;

import java.util.List;

import cam72cam.immersiverailroading.library.TrackItems;
import cam72cam.immersiverailroading.util.RailInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BuilderSwitch extends BuilderBase {

	private BuilderTurn turnBuilder;
	private BuilderStraight straightBuilder;

	public BuilderSwitch(RailInfo info, BlockPos pos) {
		super(info, pos);
		
		info.quarter = 0;
		
		RailInfo turnInfo = info.clone();
		turnInfo.type = TrackItems.TURN;

		straightBuilder = new BuilderStraight(info, pos, true);
		turnBuilder = new BuilderTurn(turnInfo, pos);
		turnBuilder.overrideFlexible = true;
		
		for(TrackBase turn : turnBuilder.tracks) {
			if (turn instanceof TrackRail) {
				turn.overrideParent(new BlockPos(straightBuilder.mainX, 0, straightBuilder.mainZ));
			}
		}
		for (TrackBase straight : straightBuilder.tracks) {
			if (straight instanceof TrackGag) {
				straight.setFlexible();
			}
		}
	}
	

	@Override
	public boolean canBuild() {
		return straightBuilder.canBuild() && turnBuilder.canBuild();
	}
	
	@Override
	public void build() {
		straightBuilder.build();
		turnBuilder.build();
	}
	
	@Override
	public List<TrackBase> getTracksForRender() {
		List<TrackBase> data = straightBuilder.getTracksForRender();
		// TODO flag for in hand render
		//if (info.relativePosition) {
			data.addAll(turnBuilder.getTracksForRender());
		//}
		return data;
	}
	
	@Override
	public List<VecYawPitch> getRenderData() {
		List<VecYawPitch> data = straightBuilder.getRenderData();
		// TODO flag for in hand render
		//if (info.relativePosition) {
			data.addAll(turnBuilder.getRenderData());
		//}
		return data;
	}


	public boolean isOnStraight(Vec3d position) {
		System.out.println(info.placementPosition);
		for(TrackBase gag : straightBuilder.tracks) {
			if (gag.getPos().add(new BlockPos(info.placementPosition)).equals(new BlockPos(position))) {
				return true;
			}
		}
		return false;
	}
}