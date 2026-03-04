package cz.raixo.blocks.block.cooldown;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class ActiveCoolDown {

    private final Date end;
    private final CompletableFuture<Void> future;
    private final WrappedTask updateTask;
}
