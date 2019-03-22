import json
import math
import uuid

import olca
import olca.pack
import fedelemflowlist


def _uid(*args) -> str:
    """ A helper function that creates a name based UUID. """
    path = '/'.join([str(arg).strip() for arg in args]).lower()
    return str(uuid.uuid3(uuid.NAMESPACE_OID, path))


def _isnil(val) -> bool:
    if val is None:
        return True
    if isinstance(val, float):
        return math.isnan(val)
    if isinstance(val, str):
        return val.strip() == ""
    return False


def _isnum(val) -> bool:
    if isinstance(val, (float, int)):
        return not math.isnan(val)
    return False


def _catpath(*args) -> str:
    p = ''
    for arg in args:
        if _isnil(arg):
            continue
        if p != '':
            p = p + "/"
        p = p + str(arg).strip()

    if p == 'air':
        return 'Elementary flows/emission/air'
    if p == 'ground':
        return 'Elementary flows/resource/ground'
    if p == 'soil':
        return 'Elementary flows/emission/soil'
    if p == 'water':
        return 'Elementary flows/emission/water'
    return p


def _s(val) -> str:
    if _isnil(val):
        return None
    return str(val).strip()


class MapFlow(object):

    def __init__(self):
        self.name = None      # type: str
        self.uid = None       # type: str
        self.category = None  # type: str
        self.property = None  # type: str
        self.unit = None      # type: str

    def to_json(self) -> dict:
        flow_ref = olca.FlowRef()
        flow_ref.name = self.name
        if self.category is not None:
            flow_ref.category_path = self.category.split('/')
        if self.uid is None:
            flow_ref.id = _uid(olca.ModelType.FLOW,
                               self.category, self.name)
        else:
            flow_ref.id = self.uid
        return {
            'flow': flow_ref.to_json()
        }


class MapEntry(object):
    """ Describes a mapping entry in the Fed.LCA flow list. """

    def __init__(self, row):

        # source flow attributes
        self.source_list = _s(row['SourceListName'])

        self.source_flow = MapFlow()
        self.source_flow.name = _s(row['SourceFlowName'])
        self.source_flow.uid = _s(row['SourceFlowUUID'])
        self.source_flow.category = _catpath(row['SourceFlowCategory1'],
                                             row['SourceFlowCategory2'],
                                             row['SourceFlowCategory3'])
        self.source_flow.property = _s(row['SourceProperty'])
        self.source_flow.unit = _s(row['SourceUnit'])

        # traget flow attributs
        self.target_flow = MapFlow()
        self.target_flow.name = _s(row['TargetFlowName'])
        self.target_flow.uid = _s(row['TargetFlowUUID'])
        self.target_flow.category = _catpath(row['TargetFlowCategory1'],
                                             row['TargetFlowCategory2'],
                                             row['TargetFlowCategory3'])
        self.target_flow.property = _s(row['TargetProperty'])
        self.target_flow.unit = _s(row['TargetUnit'])

        factor = row['ConversionFactor']
        if _isnum(factor):
            self.factor = factor
        else:
            self.factor = 1.0

    def to_json(self) -> dict:
        return {
            'from': self.source_flow.to_json(),
            'to': self.target_flow.to_json(),
            'conversionFactor': self.factor,
        }


def main():
    # returns a pandas DataFrame
    print('read CSV mapping ...')
    flow_map = fedelemflowlist.get_flowmapping(version='0.1')

    print('convert mappings ...')
    maps = {}
    for i, row in flow_map.iterrows():
        me = MapEntry(row)
        m = maps.get(me.source_list)
        if m is None:
            m = []
            maps[me.source_list] = m
        m.append(me)

    print('create flow maps ...')
    flow_maps = []
    for source_list, entries in maps.items():
        list_ref = olca.Ref()
        list_ref.olca_type = 'FlowMap'
        list_ref.name = source_list
        mappings = []
        flow_map = {
            '@id': str(uuid.uuid4()),
            'name': '%s -> Fed.LCA Commons' % source_list,
            'source': list_ref.to_json(),
            'mappings': mappings
        }
        for e in entries:
            mappings.append(e.to_json())
        flow_maps.append(flow_map)

    print('add maps to flow pack ...')
    w = olca.pack.Writer('../target/FedElemFlowList_0.2_json-ld.zip')
    for flow_map in flow_maps:
        w.write_json(flow_map, 'flow_mappings')
    w.close()
    print('all done')

if __name__ == "__main__":
    main()
