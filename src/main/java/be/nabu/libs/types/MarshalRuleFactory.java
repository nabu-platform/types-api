/*
* Copyright (C) 2014 Alexander Verbruggen
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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
