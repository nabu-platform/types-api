package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.MarshalRuleProvider;

public class MarshalRuleFactory implements MarshalRuleProvider {

	private List<MarshalRuleProvider> providers;
	private static MarshalRuleFactory instance;
	
	public static MarshalRuleFactory getInstance() {
		if (instance == null) {
			synchronized(MarshalRuleFactory.class) {
				if (instance == null) {
					instance = new MarshalRuleFactory();
				}
			}
		}
		return instance;
	}
	
	@Override
	public MarshalRule getMarshalRule(Class<?> clazz) {
		for (MarshalRuleProvider provider : getProviders()) {
			MarshalRule marshalRule = provider.getMarshalRule(clazz);
			if (marshalRule != null) {
				return marshalRule;
			}
		}
		return null;
	}
	
	private List<MarshalRuleProvider> getProviders() {
		if (providers == null) {
			synchronized(this) {
				if (providers == null) {
					List<MarshalRuleProvider> providers = new ArrayList<MarshalRuleProvider>();
					ServiceLoader<MarshalRuleProvider> serviceLoader = ServiceLoader.load(MarshalRuleProvider.class);
					for (MarshalRuleProvider provider : serviceLoader) {
						providers.add(provider);
					}
					this.providers = providers;
				}
			}
		}
		return providers;
	}

}
