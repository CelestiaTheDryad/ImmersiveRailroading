package cam72cam.immersiverailroading.sound;

import java.net.URL;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import paulscode.sound.SoundSystem;

public class ClientSound implements ISound {
	private final static float dopplerScale = 0.05f;
	private final String id;
	private SoundSystem sndSystem;
	private URL resource;
	private boolean repeats;
	private ResourceLocation oggLocation;
	private float attenuationDistance;
	private Vec3d pos;
	private Vec3d velocity;
	private float lastPitch = 1;
	
	/*
	 * TODO figure out snd system reload!
	 */

	public ClientSound(String identifier, SoundSystem sndSystem, ResourceLocation oggLocation, URL resource, boolean repeats, float attenuationDistance) {
		this.id = identifier;
		this.sndSystem = sndSystem;
		this.resource = resource;
		this.repeats = repeats;
		this.oggLocation = oggLocation;
		this.attenuationDistance = attenuationDistance;
		
		this.init();
	}
	
	public void init() {
		sndSystem.newSource(false, id, resource, oggLocation.toString(), repeats, 0f, 0f, 0f, AttenuationType.LINEAR.getTypeInt(), attenuationDistance);
	}
	
	@Override
	public void play(float pitch, float vol, Vec3d pos) {
		stop();
		setPosition(pos);
		setPitch(pitch);
		setVolume(vol);
        sndSystem.play(id);
    }
	
	@Override
	public void play() {
		sndSystem.play(id);
	}
	
	@Override
	public void setPosition(Vec3d pos) {
		this.pos = pos;
		
		sndSystem.setPosition(id, (float)pos.x, (float)pos.y, (float)pos.z);
	}

	@Override
	public void update(Vec3d pos, Vec3d vel) {
		if (!this.isPlaying()) {
			return;
		}
		
		this.setPosition(pos);
		this.setVelocity(vel);
		this.setPitch(lastPitch);
	}

	@Override
	public void setPitch(float f) {
		this.lastPitch  = f;
		if (this.pos == null || this.velocity == null) {
			sndSystem.setPitch(id, f);
		} else {
			//Doppler shift
			
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			Vec3d ppos = player.getPositionVector();
			Vec3d nextPpos = ppos.addVector(player.motionX, player.motionY, player.motionZ);
			
			Vec3d nextPos = this.pos.add(velocity);
			
			double origDist = ppos.subtract(pos).lengthVector();
			double newDist = nextPpos.subtract(nextPos).lengthVector();
			
			if (origDist > newDist) {
				f *= 1 + (origDist-newDist) * dopplerScale;
			} else {
				f *= 1 - (newDist-origDist) * dopplerScale;
			}
			
			
			sndSystem.setPitch(id, f);
		}
	}
	
	@Override
	public void setVelocity(Vec3d vel) {
		this.velocity = vel;
	}
	
	@Override
	public void setVolume(float f) {
		sndSystem.setVolume(id, f);
	}
	
	@Override
	public boolean isPlaying() {
		return sndSystem.playing(id);
	}

	@Override
	public void stop() {
		if (isPlaying()) {
			sndSystem.stop(id);
		}
	}

	@Override
	public void terminate() {
		sndSystem.removeSource(id);
	}
}
