package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;

// TODO: anything we need to do to ensure proper synchronization of the metadata files and actual files?
// Not if we change this to an a-sync file channel and do the writing from the channel handler event thread...
public class MetadataUtils {
	public static Map<String, String> getMetadata(String fsRoot, IResourceIdentifier identifier) {
		String path = Utils.constructMetadataPath(fsRoot, identifier);
		return getMetadata(path);
	}

	public static void updateMetadata(Map<String, String> newMetadata,
			Map<String, String> previousMetadata,
			AsynchronousFileChannel channel) {
		
		for (Map.Entry<String, String> mdEntry : newMetadata.entrySet()) {
			previousMetadata.put(mdEntry.getKey(), mdEntry.getValue());
		}
		
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> mdEntry : previousMetadata.entrySet()) {
			result.append(mdEntry.getKey()).append("=").append(mdEntry.getValue()).append("\n");
		}
		
		byte [] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
		
		channel.write(ByteBuffer.wrap(bytes), 0);
	}

	public static Map<String, String> getMetadata(String path) {
		Map<String, String> result = new LinkedHashMap<>();

		try {
			List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);

			for (String line : lines) {
				String [] parts = line.split("=");
				result.put(parts[0], parts[1]);
			}
		} catch (IOException e) {
			result = Collections.EMPTY_MAP;
		}

		return result;
	}
}
