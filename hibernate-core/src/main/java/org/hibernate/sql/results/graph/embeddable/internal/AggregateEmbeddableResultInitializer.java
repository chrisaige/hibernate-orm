/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.graph.embeddable.internal;

import org.hibernate.metamodel.mapping.EmbeddableMappingType;
import org.hibernate.sql.ast.spi.SqlSelection;
import org.hibernate.sql.results.graph.AssemblerCreationState;
import org.hibernate.sql.results.graph.FetchParentAccess;
import org.hibernate.sql.results.graph.embeddable.AbstractEmbeddableInitializer;
import org.hibernate.sql.results.graph.embeddable.EmbeddableResultGraphNode;
import org.hibernate.sql.results.jdbc.spi.RowProcessingState;

/**
 * An initializer for an embeddable that is mapped as aggregate e.g. STRUCT, JSON or XML.
 * The aggregate selection reads an Object[] from JDBC which serves as data for the nested {@link org.hibernate.sql.results.graph.DomainResultAssembler}.
 * This class exposes the Object[] of the aggregate to the nested assemblers through a wrapping {@link RowProcessingState}.
 */
public class AggregateEmbeddableResultInitializer extends AbstractEmbeddableInitializer implements AggregateEmbeddableInitializer {

	private final int[] aggregateValuesArrayPositions;
	private NestedRowProcessingState nestedRowProcessingState;

	public AggregateEmbeddableResultInitializer(
			FetchParentAccess fetchParentAccess,
			EmbeddableResultGraphNode resultDescriptor,
			AssemblerCreationState creationState, SqlSelection structSelection) {
		super( resultDescriptor, fetchParentAccess, creationState );
		this.aggregateValuesArrayPositions = AggregateEmbeddableInitializer.determineAggregateValuesArrayPositions(
				fetchParentAccess,
				structSelection
		);
		super.initializeAssemblers( resultDescriptor, creationState, resultDescriptor.getReferencedMappingType() );
	}

	@Override
	protected void initializeAssemblers(
			EmbeddableResultGraphNode resultDescriptor,
			AssemblerCreationState creationState,
			EmbeddableMappingType embeddableTypeDescriptor) {
		// No-op as we need to initialize assemblers in the constructor after the aggregateValuesArrayPositions is set
	}

	@Override
	public int[] getAggregateValuesArrayPositions() {
		return aggregateValuesArrayPositions;
	}

	@Override
	public Object getParentKey() {
		return findFirstEntityDescriptorAccess().getParentKey();
	}

	@Override
	public NestedRowProcessingState wrapProcessingState(RowProcessingState processingState) {
		if ( nestedRowProcessingState != null ) {
			return nestedRowProcessingState;
		}
		return nestedRowProcessingState = NestedRowProcessingState.wrap( this, processingState );
	}

}
