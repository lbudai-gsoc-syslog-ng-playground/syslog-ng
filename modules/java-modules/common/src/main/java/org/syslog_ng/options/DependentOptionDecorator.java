/*
  * Copyright (c) 2016 Balabit
  * Copyright (c) 2016 Laszlo Budai <laszlo.budai@balabit.com>
  *
  * This library is free software; you can redistribute it and/or
  * modify it under the terms of the GNU Lesser General Public
  * License as published by the Free Software Foundation; either
  * version 2.1 of the License, or (at your option) any later version.
  *
  * This library is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this library; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
  *
  * As an additional exemption you are allowed to compile & link against  the
  * OpenSSL libraries as published by the OpenSSL project. See the file
  * COPYING for details.
  *
  */

package org.syslog_ng.options;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class DependentOptionDecorator extends OptionDecorator {
	private List<Option> dependencies;

	public DependentOptionDecorator(Option decoratedOption, Option... deps) {
		super(decoratedOption);
		this.dependencies = new ArrayList<Option>(Arrays.asList(deps));
	}

	@Override
	public void validate() throws InvalidOptionException {
		decoratedOption.validate();
		for (Option o : dependencies) {
			String value = o.getValue();
			if (value == null) {
				throw new  InvalidOptionException("option " + o.getName() + " is a dependency of " + this.getName());
			}
		}
	}
}
