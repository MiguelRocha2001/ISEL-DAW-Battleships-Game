import { Logger } from "tslog";

const logger = new Logger({ name: "Siren" });

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

function extractInfoLink(linksArg: Link[]): string | undefined {
    if (!linksArg) return undefined
    return extractLink(linksArg, "server-info")
}

function extractBattleshipRanksLink(linksArg: Link[]): string {
    return extractLink(linksArg, "user-stats")
}

function extractLink(linksArg: Link[], rel: string): string {
    for (let i = 0; i < linksArg.length; i++) {
        const link = linksArg[i]
        for (let j = 0; j < link.rel.length; j++) {
            if (link.rel[j] === rel) {
                return link.href
            }
        }
    }
    return undefined
}

function extractTokenAction(actions: any[]): Action {
    for (let i = 0; i < actions.length; i++) {
        const action = actions[i]
        if (action.name === "create-token") {
            return action
        }
    }
    return undefined
}

function extractRegisterAction(actions: any[]): Action {
    for (let i = 0; i < actions.length; i++) {
        const action = actions[i]
        if (action.name === "create-user") {
            return action
        }
    }
    return undefined
}

function extractCreateGameAction(actions: any[]): Action {
    for (let i = 0; i < actions.length; i++) {
        const action = actions[i]
        if (action.name === "start-game") {
            return action
        }
    }
    return undefined
}

function extractGetGameLink(linksArg: Link[]): string {
    return extractLink(linksArg, "game")
}

function extractGetCurrentGameIdLink(linksArg: Link[]): string {
    return extractLink(linksArg, "current-game")
}

/**
 * Validates if all necessary fields, in [action], are present in [fields].
 */
 function validateFields(obj: any, action: Action): boolean {
    const keys = Object.keys(obj)
    for (let i = 0; i < action.fields.length; i++) {
        const field = action.fields[i]
        if (!keys.includes(field.name)) {
            logger.error("validateFields: missing required field: ", field.name)
            return false
        }
    }
    return true
}

export const Siren = {
    extractInfoLink,
    extractBattleshipRanksLink,
    extractTokenAction,
    extractRegisterAction,
    extractCreateGameAction,
    extractGetGameLink,
    extractGetCurrentGameIdLink,
    validateFields
}