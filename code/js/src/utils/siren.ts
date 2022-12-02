export type Siren = {
    class: string;
    properties: any;
    links: Link[];
    entities: Entity[];
    actions: Action[];
  };

export type Link = {
    rel: string[];
    href: string;
    title?: string;
    type?: string;
};

type Entity = {
    class: string[];
    properties: Object;
    entities: Entity[];
    links: Link[];
    actions: Action[];
    title: string;
};

export type Action = {
    name: string;
    title: string;
    method: string;
    href: string;
    type: string;
    fields: Field[];
};

type Field = {
    name: string;
    type: string;
    value: string;
};