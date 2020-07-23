package com.Patane.util.metadata;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import com.Patane.runnables.PatRunnable;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.main.PataneUtil;

public class RunnableMetaDataUtil {
	
	public static void refresh(@Nonnull List<Metadatable> metadatables, @Nonnull String name, @Nonnull PatRunnable task) {
		TrackedMetaDataUtil.remove(prepName(name, task));
		TrackedMetaDataUtil.set(metadatables, prepName(name, task), new FixedMetadataValue(PataneUtil.getInstance(), task));
	}
	
	public static void setOrReset(@Nonnull List<Metadatable> metadatables, @Nonnull String name, @Nonnull PatTimedRunnable task) {
		for(Metadatable metadatable : metadatables) {
			if(TrackedMetaDataUtil.has(metadatable, prepRegex(name))) {
				for(MetadataValue metaValue : TrackedMetaDataUtil.get(metadatable, prepRegex(name))) {
					if(!(metaValue.value() instanceof PatTimedRunnable))
						continue;
					PatTimedRunnable storedTask = (PatTimedRunnable) metaValue.value();
					
					storedTask.reset();
				}
			}
			else {
				TrackedMetaDataUtil.set(metadatable, prepName(name, task), new FixedMetadataValue(PataneUtil.getInstance(), task));
			}
		}
	}
	
	public static String prepRegex(String name) {
		return String.format("^%s(?:\\[\\d*\\])?$", name);
	}
			
	public static String prepName(String name, PatRunnable task) {
		return String.format("%s[%d]", name, task.getID());
	}
}
