#upload result data
srmcp -server_mode=passive file:////scratch/${just_name} \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/lsgrid/${srm_name}
