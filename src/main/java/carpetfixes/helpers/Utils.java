package carpetfixes.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Utils {

    //Raycast from BlockView
    public static BlockHitResult raycast(World world, BlockStateRaycastContext context) {
        return raycast(context.getStart(), context.getEnd(), context, (contextx, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            Vec3d vec3d = contextx.getStart().subtract(contextx.getEnd());
            return contextx.getStatePredicate().test(blockState) ? new BlockHitResult(contextx.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(contextx.getEnd()), false) : null;
        }, (contextx) -> {
            Vec3d vec3d = contextx.getStart().subtract(contextx.getEnd());
            return BlockHitResult.createMissed(contextx.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(contextx.getEnd()));
        });
    }

    //Raycast from BlockView
    private static <T, C> T raycast(Vec3d start, Vec3d end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory) {
        if (start.equals(end)) {
            return missFactory.apply(context);
        } else {
            double d = MathHelper.lerp(-1.0E-7D, end.x, start.x);
            double e = MathHelper.lerp(-1.0E-7D, end.y, start.y);
            double f = MathHelper.lerp(-1.0E-7D, end.z, start.z);
            double g = MathHelper.lerp(-1.0E-7D, start.x, end.x);
            double h = MathHelper.lerp(-1.0E-7D, start.y, end.y);
            double i = MathHelper.lerp(-1.0E-7D, start.z, end.z);
            int j = MathHelper.floor(g);
            int k = MathHelper.floor(h);
            int l = MathHelper.floor(i);
            boolean isPerfectDiagonal = Math.abs(j) == Math.abs(l); //This is a 45 degree angle
            BlockPos.Mutable mutable = new BlockPos.Mutable(j, k, l);
            T object = blockHitFactory.apply(context, mutable);
            if (object != null) {
                return object;
            } else {
                double m = d - g;
                double n = e - h;
                double o = f - i;
                int p = MathHelper.sign(m);
                int q = MathHelper.sign(n);
                int r = MathHelper.sign(o);
                double s = p == 0 ? 1.7976931348623157E308D : (double)p / m;
                double t = q == 0 ? 1.7976931348623157E308D : (double)q / n;
                double u = r == 0 ? 1.7976931348623157E308D : (double)r / o;
                double v = s * (p > 0 ? 1.0D - MathHelper.fractionalPart(g) : MathHelper.fractionalPart(g));
                double w = t * (q > 0 ? 1.0D - MathHelper.fractionalPart(h) : MathHelper.fractionalPart(h));
                double x = u * (r > 0 ? 1.0D - MathHelper.fractionalPart(i) : MathHelper.fractionalPart(i));
                Object object2;
                do {
                    if (!(v <= 1.0D) && !(w <= 1.0D) && !(x <= 1.0D)) {
                        return missFactory.apply(context);
                    }
                    if (v < w) {
                        if (v < x) {
                            j += p;
                            v += s;
                        } else {
                            l += r;
                            x += u;
                        }
                    } else if (w < x) {
                        k += q;
                        w += t;
                    } else {
                        l += r;
                        x += u;
                    }
                    object2 = blockHitFactory.apply(context, mutable.set(j, k, l));
                    if (isPerfectDiagonal && object2 == null) {
                        object2 = blockHitFactory.apply(context, mutable.set(l, k, j));
                    }
                } while(object2 == null);

                return (T) object2;
            }
        }
    }
}
