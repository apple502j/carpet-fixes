package carpetfixes.mixins.coreSystemFixes;

import carpetfixes.CarpetFixesSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="net/minecraft/world/chunk/WorldChunk$DirectBlockEntityTickInvoker")
public abstract class WorldChunk$DirectBlockEntityTickInvoker_illegalBlockEntityMixin <T extends BlockEntity> implements BlockEntityTickInvoker {


    /*@Shadow @Final private T blockEntity;


    @Inject(
            method="tick",
            at=@At(
                    shift= At.Shift.BEFORE,
                    value="INVOKE",
                    target="Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;[Lorg/apache/logging/log4j/util/Supplier;)V"
            ))
    public void beforeWarnClean(CallbackInfo ci) {
        if (CarpetFixesSettings.illegalBlockEntityFix) {
            BlockEntity blockEntity = this.blockEntity;
            blockEntity.getWorld().getChunk(blockEntity.getPos()).removeBlockEntity(blockEntity.getPos());
        }
    }*/
}
