/*
 * Copyright ${year} interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.interactive_instruments.etf;

import de.interactive_instruments.etf.dal.dto.test.ExecutableTestSuiteDto;
import de.interactive_instruments.etf.testdriver.DependencyGraph;
import de.interactive_instruments.exceptions.ObjectWithIdNotFoundException;
import de.interactive_instruments.exceptions.StorageException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DependencyResolverTest {

	@Test
	public void testDependencyResolving() throws StorageException, ObjectWithIdNotFoundException {

		final DependencyGraph<ExecutableTestSuiteDto> dependencyResolver = new DependencyGraph();

		final ExecutableTestSuiteDto ets1 = CoreTestUtils.createEts(1);
		final ExecutableTestSuiteDto ets2 = CoreTestUtils.createEts(2);
		final ExecutableTestSuiteDto ets3 = CoreTestUtils.createEts(3);
		final ExecutableTestSuiteDto ets4 = CoreTestUtils.createEts(4);
		final ExecutableTestSuiteDto ets5 = CoreTestUtils.createEts(5);
		final ExecutableTestSuiteDto ets6 = CoreTestUtils.createEts(6);
		final ExecutableTestSuiteDto ets7 = CoreTestUtils.createEts(7);

		ets1.addDependency(ets2);
		ets1.addDependency(ets3);
		ets1.addDependency(ets6);

		ets2.addDependency(ets4);
		ets2.addDependency(ets3);

		ets3.addDependency(ets4);
		ets3.addDependency(ets5);

		ets4.addDependency(ets6);

		ets5.addDependency(ets7);

		ets6.addDependency(ets7);

		final ArrayList<ExecutableTestSuiteDto> executableTestSuiteDtos = new ArrayList<ExecutableTestSuiteDto>() {{
			add(ets1);
			add(ets2);
			add(ets3);
			add(ets4);
			add(ets5);
			add(ets6);
			add(ets7);
		}};

		dependencyResolver.addAllDependencies(executableTestSuiteDtos);
		final List<ExecutableTestSuiteDto> sorted = dependencyResolver.sort();

		assertNotNull(sorted);
		assertEquals(7, sorted.size());


		Assert.assertEquals("ETS.7", sorted.get(6).getLabel());
		Assert.assertEquals("ETS.5", sorted.get(5).getLabel());
		Assert.assertEquals("ETS.6", sorted.get(4).getLabel());
		Assert.assertEquals("ETS.4", sorted.get(3).getLabel());
		Assert.assertEquals("ETS.3", sorted.get(2).getLabel());
		Assert.assertEquals("ETS.2", sorted.get(1).getLabel());
		Assert.assertEquals("ETS.1", sorted.get(0).getLabel());

	}

	@Test(expected = IllegalStateException.class)
	public void testCycleDetection() throws StorageException, ObjectWithIdNotFoundException {
		final DependencyGraph<ExecutableTestSuiteDto> dependencyResolver = new DependencyGraph();

		final ExecutableTestSuiteDto ets1 = CoreTestUtils.createEts(1);
		final ExecutableTestSuiteDto ets2 = CoreTestUtils.createEts(2);
		final ExecutableTestSuiteDto ets3 = CoreTestUtils.createEts(3);
		final ExecutableTestSuiteDto ets4 = CoreTestUtils.createEts(4);

		ets1.addDependency(ets2);
		ets2.addDependency(ets3);
		ets3.addDependency(ets4);
		ets4.addDependency(ets1);

		final ArrayList<ExecutableTestSuiteDto> executableTestSuiteDtos = new ArrayList<ExecutableTestSuiteDto>() {{
			add(ets1);
			add(ets2);
			add(ets3);
			add(ets4);
		}};

		dependencyResolver.addAllDependencies(executableTestSuiteDtos);
		dependencyResolver.sort();
	}
}