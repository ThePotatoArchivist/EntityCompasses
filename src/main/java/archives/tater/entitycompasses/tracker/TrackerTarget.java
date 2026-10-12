package archives.tater.entitycompasses.tracker;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.Mth.*;

public sealed interface TrackerTarget {
    None NONE = new None();

    int MAX_BLOCK_DISTANCE = 8 * 16;
    int MAX_CHUNK_DISTANCE = 16 * 16;

    StreamCodec<ByteBuf, TrackerTarget> CODEC = new StreamCodec<>() {
        public TrackerTarget decode(ByteBuf buffer) {
            return switch (buffer.readByte()) {
                case 0 -> NONE;
                case 1 -> Block.CODEC.decode(buffer);
                case 2 -> Chunk.CODEC.decode(buffer);
                case 3 -> Angle.CODEC.decode(buffer);
                default -> throw new IllegalArgumentException("Invalid Tracker Target type");
            };
        }

        @Override
        public void encode(ByteBuf buffer, TrackerTarget value) {
            switch (value) {
                case None ignored -> {
                    buffer.writeByte(0);
                }
                case Block block -> {
                    buffer.writeByte(1);
                    Block.CODEC.encode(buffer, block);
                }
                case Chunk chunk -> {
                    buffer.writeByte(2);
                    Chunk.CODEC.encode(buffer, chunk);
                }
                case Angle angle -> {
                    buffer.writeByte(3);
                    Angle.CODEC.encode(buffer, angle);
                }
            }
        }
    };
    BlockPos getApproximatePosition(Vec3 origin);

    static TrackerTarget create(@Nullable BlockPos target, Vec3 origin) {
        if (target == null) return NONE;
        var difference = target.getCenter().subtract(origin);
        var distanceSq = difference.horizontalDistanceSqr();
        if (distanceSq < MAX_BLOCK_DISTANCE * MAX_BLOCK_DISTANCE) return new Block(target);
        if (distanceSq < MAX_CHUNK_DISTANCE * MAX_CHUNK_DISTANCE) return new Chunk(new ChunkPos(target));
        return Angle.fromDifference(difference);
    }

    record Block(BlockPos pos) implements TrackerTarget {
        public static final StreamCodec<ByteBuf, Block> CODEC = BlockPos.STREAM_CODEC.map(Block::new, Block::pos);

        @Override
        public BlockPos getApproximatePosition(Vec3 origin) {
            return pos;
        }
    }

    record Chunk(ChunkPos chunk) implements TrackerTarget {
        public static final StreamCodec<ByteBuf, ChunkPos> CHUNK_POS_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, pos -> pos.x,
                ByteBufCodecs.VAR_INT, pos -> pos.z,
                ChunkPos::new
        );

        public static final StreamCodec<ByteBuf, Chunk> CODEC = CHUNK_POS_CODEC.map(Chunk::new, Chunk::chunk);

        @Override
        public BlockPos getApproximatePosition(Vec3 origin) {
            return chunk.getMiddleBlockPosition(0);
        }
    }

    record Angle(int angle) implements TrackerTarget {
        public static final double ASSUMED_DISTANCE = 256;
        public static final int INCREMENTS = 32;
        public static final StreamCodec<ByteBuf, Angle> CODEC = ByteBufCodecs.VAR_INT.map(Angle::new, Angle::angle);

        @SuppressWarnings("SuspiciousNameCombination")
        public static Angle fromDifference(Vec3 difference) {
            var angle = atan2(difference.x, difference.z);
            if (angle < 0) angle += TWO_PI;
            return new Angle((int) (INCREMENTS * angle / TWO_PI));
        }

        @Override
        public BlockPos getApproximatePosition(Vec3 origin) {
            var angle = this.angle * TWO_PI / INCREMENTS;
            return BlockPos.containing(origin.add(ASSUMED_DISTANCE * sin(angle), 0, ASSUMED_DISTANCE * cos(angle)));
        }
    }

    final class None implements TrackerTarget {
        private None() {}

        @Override
        public BlockPos getApproximatePosition(Vec3 origin) {
            return BlockPos.ZERO;
        }

        @Override
        public String toString() {
            return "None";
        }
    }
}
