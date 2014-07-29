<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/Document">
		<BooleanQuery>
		
			<!-- #1 -->
			<xsl:if test="type">
				<Clause occurs="must">
					<ConstantScoreQuery>
						<CacheFilter>
							<TermsFilter fieldName="type">
								<xsl:value-of select="type" />
							</TermsFilter>
						</CacheFilter>
					</ConstantScoreQuery>
				</Clause>
			</xsl:if>

			<!-- #2 -->
			<xsl:if test="description">
				<Clause occurs="must">
					<UserQuery fieldName="description">
						<xsl:value-of select="description" />
					</UserQuery>
				</Clause>
			</xsl:if>

			<!-- #3 -->
			<xsl:if test="South|North|East|West">
				<Clause>
					<ConstantScoreQuery>
						<BooleanFilter>
							<xsl:for-each select="Souch|North|East|West">
								<Clause occurs="should">
									<CachedFilter>
										<TermsFilter>
											<xsl:value-of select="name()" />
										</TermsFilter>
									</CachedFilter>
								</Clause>
							</xsl:for-each>
						</BooleanFilter>
					</ConstantScoreQuery>
				</Clause>
			</xsl:if>

			<!-- 4# -->
			<xsl:if test="salaryRange">
				<Clause occurs="must">
					<ConstantScoreQuery>
						<RangeFilter fieldName="salary">
							<xsl:attribute name="lowerTerm">
			                    <xsl:value-of
								select='format-number( substring-before(salaryRange,"-"), "000")' />
			                </xsl:attribute>
							<xsl:attribute name="upperTerm">
			                <xsl:value-of
								select='format-number( substring-after(salaryRange,"-"), "000")' />
			                </xsl:attribute>
						</RangeFilter>
					</ConstantScoreQuery>
				</Clause>
			</xsl:if>

		</BooleanQuery>
	</xsl:template>
</xsl:stylesheet>