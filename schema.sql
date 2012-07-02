alter table AlternateId drop foreign key FK57AEBB1523F36C11
alter table Category drop foreign key FK6DD211EDB2E9C5F
alter table Category drop foreign key FK6DD211ECDC19DD2
alter table Individual drop foreign key FK740F7EB9495FE564
alter table Individual drop foreign key FK740F7EB94172AB70
alter table Individual drop foreign key FK740F7EB934BB7609
alter table Investigation_contacts drop foreign key FK6FD9DD10ACA1B35D
alter table Investigation_contacts drop foreign key FK6FD9DD106D6CE5A
alter table Location drop foreign key FK752A03D5495FE564
alter table Measurement drop foreign key FKF75C839CA341BCBA
alter table Measurement drop foreign key FKF75C839C242A387A
alter table Measurement drop foreign key FKF75C839C9980AC36
alter table Measurement_categories drop foreign key FK3D29FE9F5AD0C62E
alter table Measurement_categories drop foreign key FK3D29FE9F204D2BE4
alter table MolgenisGroup drop foreign key FKCA024C8F5D736693
alter table MolgenisPermission drop foreign key FK65B7B9BF63EDE821
alter table MolgenisPermission drop foreign key FK65B7B9BF24478971
alter table MolgenisRoleGroupLink drop foreign key FK3190CD5363EDE821
alter table MolgenisRoleGroupLink drop foreign key FK3190CD5369C177D
alter table MolgenisUser drop foreign key FK68A93BB28A97B22
alter table ObservableFeature drop foreign key FK1AE47C13DB2E9C5F
alter table ObservationElement drop foreign key FK57E55150ACA1B35D
alter table ObservationElement drop foreign key FK57E55150CDC19DD2
alter table ObservationElement_AlternateId drop foreign key FKD900F2A6E0F19EB6
alter table ObservationElement_AlternateId drop foreign key FKD900F2A662178C94
alter table ObservationTarget drop foreign key FKC102A4BDDB2E9C5F
alter table ObservedValue drop foreign key FK46CDFB69BA228940
alter table ObservedValue drop foreign key FK46CDFB69A0D10A3A
alter table ObservedValue drop foreign key FK46CDFB69ACA1B35D
alter table ObservedValue drop foreign key FK46CDFB69A6AD00D5
alter table ObservedValue drop foreign key FK46CDFB69E7B2D4CE
alter table ObservedValue drop foreign key FK46CDFB69CDC19DD2
alter table OntologyTerm drop foreign key FKCE70D67B557474B9
alter table Panel drop foreign key FK49519E4495FE564
alter table Panel drop foreign key FK49519E4DF5D0CE4
alter table Panel drop foreign key FK49519E4CBA55634
alter table Panel_FounderPanels drop foreign key FKB7032C4373324974
alter table Panel_FounderPanels drop foreign key FKB7032C438EB82CE
alter table Panel_Individuals drop foreign key FKEE361AFF73324974
alter table Panel_Individuals drop foreign key FKEE361AFF2B96A1A7
alter table Person drop foreign key FK8E4887752A6DEDB3
alter table Person drop foreign key FK8E4887755D736693
alter table Person drop foreign key FK8E48877524771513
alter table Protocol drop foreign key FKC8E4F2B82D9B70C8
alter table Protocol drop foreign key FKC8E4F2B8ACA1B35D
alter table Protocol drop foreign key FKC8E4F2B8CDC19DD2
alter table Protocol drop foreign key FKC8E4F2B861609227
alter table ProtocolApplication drop foreign key FKB6AC57789A9D4272
alter table ProtocolApplication drop foreign key FKB6AC5778ACA1B35D
alter table ProtocolApplication drop foreign key FKB6AC5778CDC19DD2
alter table ProtocolApplication_Performer drop foreign key FKF001BA7E7B2D4CE
alter table ProtocolApplication_Performer drop foreign key FKF001BA745570DD5
alter table ProtocolDocument drop foreign key FK1E39EFF39A9D4272
alter table ProtocolDocument drop foreign key FK1E39EFF35C063302
alter table Protocol_Features drop foreign key FKF14116049A9D4272
alter table Protocol_Features drop foreign key FKF141160491EE93DC
alter table Protocol_subprotocols drop foreign key FK2DF430C29A9D4272
alter table Protocol_subprotocols drop foreign key FK2DF430C2B74A4275
alter table Publication drop foreign key FK23254A0CEEC12EE8
alter table Publication drop foreign key FK23254A0C23F4E434
alter table Publication drop foreign key FK23254A0C711A7200
alter table Species drop foreign key FKEB81D91C23F36C11
alter table Workflow drop foreign key FK5F63BDFD592C0F5
alter table WorkflowElement drop foreign key FKDBE3321D9A9D4272
alter table WorkflowElement drop foreign key FKDBE3321D14BFD4C0
alter table WorkflowElementParameter drop foreign key FKC3D7B20C17C06348
alter table WorkflowElementParameter drop foreign key FKC3D7B20CE56A2918
alter table WorkflowElement_PreviousSteps drop foreign key FKCBA127AEE56A2918
alter table WorkflowElement_PreviousSteps drop foreign key FKCBA127AE228AA14B
drop table if exists AlternateId
drop table if exists Category
drop table if exists Individual
drop table if exists Institute
drop table if exists Investigation
drop table if exists Investigation_contacts
drop table if exists Location
drop table if exists Measurement
drop table if exists Measurement_categories
drop table if exists MolgenisEntity
drop table if exists MolgenisFile
drop table if exists MolgenisGroup
drop table if exists MolgenisPermission
drop table if exists MolgenisRole
drop table if exists MolgenisRoleGroupLink
drop table if exists MolgenisUser
drop table if exists ObservableFeature
drop table if exists ObservationElement
drop table if exists ObservationElement_AlternateId
drop table if exists ObservationTarget
drop table if exists ObservedValue
drop table if exists Ontology
drop table if exists OntologyTerm
drop table if exists Panel
drop table if exists Panel_FounderPanels
drop table if exists Panel_Individuals
drop table if exists Person
drop table if exists Protocol
drop table if exists ProtocolApplication
drop table if exists ProtocolApplication_Performer
drop table if exists ProtocolDocument
drop table if exists Protocol_Features
drop table if exists Protocol_subprotocols
drop table if exists Publication
drop table if exists RuntimeProperty
drop table if exists Species
drop table if exists UseCase
drop table if exists Workflow
drop table if exists WorkflowElement
drop table if exists WorkflowElementParameter
drop table if exists WorkflowElement_PreviousSteps
