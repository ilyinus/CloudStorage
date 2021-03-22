package filewatcher;

import actions.Action;
import actions.ActionType;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    private Path folder;
    private BlockingQueue<Action> queue;

    public FileWatcher(Path folder, BlockingQueue<Action> queue) {
        this.folder = folder;
        this.queue = queue;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            folder.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            boolean poll = true;
            while (poll) {
                poll = pollEvents(watchService);
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        List<WatchEvent<?>> eventList = key.pollEvents();

        if (eventList.size() == 2 &&
                eventList.get(0).kind() == ENTRY_DELETE &&
                eventList.get(1).kind() == ENTRY_CREATE) {

            queue.put(new Action(path.resolve((Path) eventList.get(0).context()),
                    path.resolve((Path) eventList.get(1).context()),
                    ActionType.RENAME_FILE));

        } else if (eventList.size() == 1) {

            WatchEvent<?> event = eventList.get(0);

            if (event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY) {
                queue.put(new Action(path.resolve((Path) event.context()), ActionType.DELIVER_DATA));
            } else if (event.kind() == ENTRY_DELETE) {
                queue.put(new Action(path.resolve((Path) event.context()), ActionType.DELETE_FILE));
            }

        }

        return key.reset();

    }

}
