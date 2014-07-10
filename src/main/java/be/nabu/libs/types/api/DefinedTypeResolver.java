package be.nabu.libs.types.api;

import be.nabu.libs.artifacts.api.ArtifactResolver;

public interface DefinedTypeResolver extends ArtifactResolver<DefinedType> {
	public DefinedType resolve(String id);
}
